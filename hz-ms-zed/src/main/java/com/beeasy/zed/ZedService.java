package com.beeasy.zed;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DB2SqlStyle;
import org.beetl.sql.core.engine.PageQuery;
import org.beetl.sql.ext.DebugInterceptor;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;

import javax.jms.*;
import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//@Service
//@Transactional
class ZedService {

    private DeconstructService deconstructService;
    private DataSource dataSource;
//    @Autowired
    public SQLManager sqlManager;
    public AtomicReference<SQLManager> atomSM = new AtomicReference<>();
    public JSONObject config;

    private static final String FIELD_LINK = "link";
    private static final String FIELD_PAGE = "Page";
    private static final String FIELD_SIZE = "Size";
    private static final String FIELD_AND = "And";
    private static final String FIELD_OR = "Or";
    private static final Set<String> FIELDS = C.newSet(FIELD_PAGE, FIELD_SIZE, FIELD_AND, FIELD_OR);
    private static final Pattern reg = Pattern.compile("\\.|\\s*->\\s*|\\.");

    /**
     * @param params
     */
    public JSONObject select(JSONObject params) {
        Map<String, LinkNode> links = parseLink(params.get(FIELD_LINK));
        params.remove(FIELD_LINK);

        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            selectSingle(entry.getKey(), (JSONObject) entry.getValue(), null, links, new ArrayList<>(), result);
        }

        return result;
    }

    private void selectSingle(String sourceTable, JSONObject value, String linkTable, Map<String, LinkNode> links, List<JSONObject> result, JSONObject finalResult) {
        String targetTable = sourceTable.toLowerCase();

        //数组查询
        int idex = sourceTable.indexOf("[]");
        boolean multipul = false;
        if (idex > -1) {
            targetTable = sourceTable.replace("[]", "").toLowerCase();
            multipul = true;
        }
        //分页查询
        int page = -1;
        int size = -1;
        int total = -1;
        if (value.containsKey(FIELD_PAGE)) {
            page = value.getInt(FIELD_PAGE, 1);
            value.remove(FIELD_PAGE);
            if (page < 0) {
                page = 1;
            }
        }
        if (value.containsKey(FIELD_SIZE)) {
            size = value.getInt(FIELD_SIZE, 10);
            value.remove(FIELD_SIZE);
        }
        if (size < 0) {
            size = 10;
        }


        //查询附加表
        JSONObject externals = new JSONObject();
        Iterator<Map.Entry<String, Object>> iterator = value.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            if(FIELDS.contains(entry.getKey())){
                continue;
            }
            if(isUpper(entry.getKey().charAt(0))){
                iterator.remove();
                externals.put(entry.getKey(), entry.getValue());
            }
        }
        JSONObject beetlParams = new JSONObject();
        //拼装条件
        StringBuilder where = buildWhere(value, beetlParams);

        //主关联
        String selectFields = "*";
        String table = targetTable;
        String join = "";
        String[] tfs = new String[0];
        scan:
        {
            if (linkTable == null) {
                break scan;
            }
            LinkNode node = links.get(linkTable);
            if (node == null) {
                break scan;
            }
            if (result.size() == 0) {
                break scan;
            }
            //查询关联
            List<LinkPath> paths = findLink(links.get(linkTable), targetTable, null)
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.stack.size()))
                    .collect(Collectors.toList());
            //如果有，那么只取第一个最小的
            if (paths.size() == 0) {
                break scan;
            }
            // TODO: 2019/3/27 根据单双进行关联
            LinkPath path;
            boolean flag = paths.size() > 2 && paths.get(0).stack.size() == 1 && paths.get(1).stack.size() == 1;
            if (flag) {
                if (multipul) {
                    path = paths.get(0);
                } else {
                    path = paths.get(1);
                }
            } else {
                path = paths.get(0);
            }
            join = String.join(" ", path.linkStrs);
            final String[] finalTfs1 = tfs = path.stack.get(0).split("\\|");
            if (flag) {
                if (!multipul) {
                    String tmp = tfs[1];
                    tfs[1] = tfs[2];
                    tfs[2] = tmp;
                }
            }
            if(where.length() > 0){
                where.append(" and");
            }
            where.append(S.fmt(" t0.%s in (#join(keyx)#)", tfs[1]));
            beetlParams.put(
                    "keyx",
                    result.stream()
                            .map(i -> i.getStr(finalTfs1[1]))
                            .collect(Collectors.toSet())
            );
            selectFields = S.fmt("t%d.*, t0.%s as zed_id", path.linkStrs.size(), tfs[1]);
            table = linkTable;

        }

        if(where.length() > 0){
            where.insert(0, "where");
        }

        //如果使用了分页查询
        List<JSONObject> ret = null;
        if (page > -1) {
            String countFields = S.fmt("count(*) as total_num", selectFields);
            String sql = S.fmt("select %s from %s t0 %s %s", countFields, table, join, where.toString());
            List<JSONObject> countRet = sqlManager.execute(sql, JSONObject.class, beetlParams);
            total = countRet.get(0).getInt("totalNum", 0);
            if (countRet.size() > 0 && total > 0) {
                sql = S.fmt("select %s from %s t0 %s %s", selectFields, table, join, where.toString());
                ret = sqlManager.execute(sql, JSONObject.class, beetlParams, (page - 1) * size + 1, size);
            }
        } else {
            String sql = S.fmt("select %s from %s t0 %s %s", selectFields, table, join, where.toString());
            ret = sqlManager.execute(sql, JSONObject.class, beetlParams);
        }
        if (ret == null) {
            ret = new ArrayList<>();
        }

        if (linkTable != null && tfs.length > 0) {
            String[] finalTfs = tfs;
            for (JSONObject object : result) {
                List<JSONObject> items = ret.stream()
                        .filter(i -> S.eq(i.getStr("zedId"), object.getStr(finalTfs[1])))
                        .collect(Collectors.toList());
                if (multipul) {
                    // TODO: 2019/3/27 关联查询也应支持分页
                    object.put(sourceTable, items);
                } else if (items.size() > 0) {
                    object.put(sourceTable, items.get(0));
                } else {
                    object.put(sourceTable, new JSONObject());
                }
            }
        }
        //构造附加表
        for (Map.Entry<String, Object> entry : externals.entrySet()) {
            selectSingle(entry.getKey(), (JSONObject) entry.getValue(), targetTable, links, ret, null);
        }

        if (multipul) {
            if (finalResult != null) {
                if (total > -1) {
                    PageQuery<JSONObject> pageQuery = new PageQuery<>();
                    pageQuery.setList(ret);
                    pageQuery.setPageNumber(page);
                    pageQuery.setPageSize(size);
                    pageQuery.setTotalRow(total);
                    finalResult.put(sourceTable, pageQuery);
                } else {
                    finalResult.put(sourceTable, ret);
                }
            }
        } else {
            if (finalResult != null && ret.size() > 0) {
                //单独查询不存在分页
                finalResult.put(sourceTable, ret.get(0));
            }
        }
    }

    /**
     * 查找关联
     * @param fromNode
     * @param to
     * @param path
     * @return
     */
    private List<LinkPath> findLink(LinkNode fromNode, String to, LinkPath path) {
        List<LinkPath> result = new ArrayList<>();
        if (fromNode == null) {
            return result;
        }
        if (path == null) {
            path = new LinkPath();
        }
        for (Map.Entry<String, List<LinkNode>> entry : fromNode.links.entrySet()) {
            //如果有更大的，放弃
            if (result.size() > 0) {
                if (path.stack.size() >= result.get(0).stack.size()) {
                    return result;
                }
            }
            String key = S.fmt("%s|%s", fromNode.name, entry.getKey());
            String[] fields = entry.getKey().split("\\|");
            if (path.stack.contains(key)) {
                continue;
            }
            for (LinkNode linkNode : entry.getValue()) {
                LinkPath cp = $.deepCopy(path).to(new LinkPath());
                cp.stack.add(key);
                cp.linkStrs.add(
                        S.fmt(" join %s t%d on t%d.%s = t%d.%s", linkNode.name, path.linkStrs.size() + 1, path.linkStrs.size(), fields[0], path.linkStrs.size() + 1, fields[1])
                );

                if (S.eq(linkNode.name, to)) {
                    result.add(cp);
                } else {
                    result.addAll(
                            findLink(linkNode, to, cp)
                    );
                }
            }
        }

        return result;
    }


    /**
     * 构造WHERE条件
     * @param params
     * @param beetlParams
     * @return
     */
    private StringBuilder buildWhere(JSONObject params, JSONObject beetlParams) {
        boolean zero = true;
        StringBuilder sb = new StringBuilder();
        List<Map.Entry<String, Object>> defer = new ArrayList<>();
        build:
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            //延后处理
            switch (entry.getKey()){
                case FIELD_AND:
                case FIELD_OR:
                    defer.add(entry);
                    continue build;

            }
            if(value instanceof JSONArray){

            } else if (value instanceof JSONObject){

            } else {
                String key = "key" + (beetlParams.size());
                if(zero){
                    zero = false;
                } else {
                   sb.append(" and");
                }
                sb.append(S.fmt(" %s = #%s#", entry.getKey(), key));
                beetlParams.put(key, entry.getValue());
            }
        }

        for (Map.Entry<String, Object> entry : defer) {
            switch (entry.getKey()){
                case FIELD_AND:
                    StringBuilder andsb = buildWhere((JSONObject) entry.getValue(), beetlParams);
                    if(andsb.length() > 0 ){
                        if(zero){
                            zero = false;
                        } else {
                            sb.append(" and");
                        }
                        sb.append("(");
                        sb.append(andsb);
                        sb.append(")");
                    }
                    break;

                case FIELD_OR:
                    StringBuilder orsb = buildWhere((JSONObject) entry.getValue(), beetlParams);
                    if(orsb.length() > 0 ){
                        sb.append(" or(");
                        sb.append(orsb);
                        sb.append(")");
                    }
                    break;
            }
        }
        return sb;
    }

    /**
     * @param links
     * @return
     */
    private Map<String, LinkNode> parseLink(Object links) {
        Map<String, LinkNode> map = C.newMap();
        if (links == null) {
            return map;
        }
        if (!(links instanceof JSONArray)) {
            return map;
        }
        JSONArray arr = (JSONArray) links;
        for (int i = 0; i < arr.size(); i++) {
            String link = arr.getStr(i).toLowerCase();
            String[] sides = reg.split(link);
            if (sides.length != 4) {
                continue;
            }
            LinkNode left = getOrCreateLinkNode(map, sides[0]);
            LinkNode right = getOrCreateLinkNode(map, sides[2]);
            String leftKey = sides[1] + "|" + sides[3];
            String rightKey = sides[3] + "|" + sides[1];
            List<LinkNode> leftLinks = getOrCreateLinkNodeList(left.links, leftKey);
            List<LinkNode> rightLinks = getOrCreateLinkNodeList(right.links, rightKey);
            leftLinks.add(right);
            rightLinks.add(left);
        }
        return map;
    }


    private LinkNode getOrCreateLinkNode(Map<String, LinkNode> map, String key) {
        LinkNode node = map.get(key);
        if (node == null) {
            node = new LinkNode();
            node.name = key;
            map.put(key, node);
        }
        return node;
    }

    private List<LinkNode> getOrCreateLinkNodeList(Map map, String key) {
        List list = (List) map.get(key);
        if (list == null) {
            list = new ArrayList();
            map.put(key, list);
        }
        return list;
    }

    /**
     * @param s
     * @return
     */
    private boolean isUpper(String s) {
        return S.eq(s.toUpperCase(), s);
    }

    /**
     * @param c
     * @return
     */
    private boolean isUpper(char c) {
        return isUpper(String.valueOf(c));
    }

    public static class LinkNode {
        String name;
        Map<String, List<LinkNode>> links = new HashMap<>();
    }

    public static class LinkPath {
        List<String> linkStrs = new ArrayList<>();
        List<String> stack = new ArrayList<>();
    }

    /***********************************************/

    /**
     * 以下为不用springboot时所需要的方法
     */

    /***********************************************/


    /**
     * netty 服务
     */
    public Object doNettyRequest(ChannelHandlerContext ctx, FullHttpRequest request){
        System.out.println("fuck");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(true)
            return null;

        if(!request.method().equals(HttpMethod.POST)){
            return null;
        }
        if(request.content().isReadable()){
            String json = request.content().toString(CharsetUtil.UTF_8);
            return select(JSONUtil.parseObj(json));
        }
        return null;
    }
//
//    public Object doDeconstruct(ChannelHandlerContext ctx, FullHttpRequest req){
//        return deconstructService.doNettyRequest(ctx, req);
//    }



    public void initDB(boolean dev){
        JSONObject ds = config.getJSONObject("datasource");

        ConnectionSource source;
        if(dev){
            //实例化类
            HikariConfig hikariConfig = new HikariConfig();
            //设置url
            hikariConfig.setJdbcUrl(ds.getStr("url"));
            //数据库帐号
            hikariConfig.setUsername(ds.getStr("username"));
            //数据库密码
            hikariConfig.setPassword(ds.getStr("password"));
            hikariConfig.setDriverClassName(ds.getStr("driver"));
            hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
//            hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(hikariConfig);
            source =  ConnectionSourceHelper.getSingle(dataSource);
        } else {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(ds.getStr("driver"));
            druidDataSource.setUrl(ds.getStr("url"));
            druidDataSource.setUsername(ds.getStr("username"));
            druidDataSource.setPassword(ds.getStr("password"));
            druidDataSource.setAsyncInit(true);
            dataSource = druidDataSource;
            source = ConnectionSourceHelper.getSingle(druidDataSource);
        }
        SQLLoader loader = new ClasspathLoader("/sql");
        UnderlinedNameConversion nc = new  UnderlinedNameConversion();
        sqlManager = new SQLManager(new DB2SqlStyle(),loader,source,nc,new Interceptor[]{new DebugInterceptor()});

        atomSM.set(sqlManager);
    }

    public void initConfig(){
        ClassPathResource resource = new ClassPathResource("config.json");
        String content = IoUtil.read(resource.getStream(), CharsetUtil.UTF_8);
        config = JSONUtil.parseObj(content);
    }

    public void initNetty(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)

//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        public void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline pipeline = ch.pipeline();
//
//                            pipeline.addLast(new HttpServerCodec());
//                            pipeline.addLast( new HttpObjectAggregator(1024 * 1024));
//                            pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
//                            pipeline.addLast(new HttpServerHandler());
//                        }
//                    })
                .childHandler(new ChannelInitializer<SocketChannel>(){


                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //将请求和应答消息编码或解码为HTTP消息
                        pipeline.addLast(new HttpServerCodec());
                        //将HTTP消息的多个部分组合成一条完整的HTTP消息
                        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
                        pipeline.addLast(new StringDecoder(Charset.forName("UTF-8")));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new HttpStaticHandleAdapter());
                        pipeline.addLast(new HttpServerHandler());

                    }
                });
            ThreadUtil.execAsync(() -> {
                System.out.println("boot success");
            });
            ChannelFuture f = b.bind(config.getInt("port")).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }



}
