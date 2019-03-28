package com.beeasy.zed;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.javafx.sg.prism.NodePath;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.ServiceMode;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.PAGE_DOWN;
import static javafx.scene.input.KeyCode.T;

@Service
@Transactional
class ZedService {

    @Autowired
    SQLManager sqlManager;

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
            page = value.getInteger(FIELD_PAGE);
            value.remove(FIELD_PAGE);
            if (page < 0) {
                page = 1;
            }
        }
        if (value.containsKey(FIELD_SIZE)) {
            size = value.getInteger(FIELD_SIZE);
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
                            .map(i -> i.getString(finalTfs1[1]))
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
            total = countRet.get(0).getInteger("totalNum");
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
                        .filter(i -> S.eq(i.getString("zedId"), object.getString(finalTfs[1])))
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
        for (short i = 0; i < arr.size(); i++) {
            String link = arr.getString(i).toLowerCase();
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
}
