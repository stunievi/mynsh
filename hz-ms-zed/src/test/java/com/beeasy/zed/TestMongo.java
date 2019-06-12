package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.beetl.sql.core.SQLReady;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.E;
import org.osgl.util.S;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static com.beeasy.zed.DBService.dataSource;
import static com.beeasy.zed.DBService.sqlManager;

public class TestMongo {
    private static MongoService mongoService = new MongoService();
    private static DeconstructService deconstructService = new DeconstructService();
    public static ConcurrentMap<String, String> concurrentMapWordCounts = new ConcurrentHashMap<>();

    @BeforeClass
    public static void onBefore() throws ExecutionException, InterruptedException {
        mongoService.start();
        DBService.getInstance().initSync();
        deconstructService.initSync();
        vvv();
    }

    public  static void vvv(){
        JSONObject jsonObject = JSONObject.parseObject(Utils.readFile().toString());
        JSONArray array = jsonObject.getJSONArray("Result");
        for (Object o : array) {
            JSONObject j = JSONObject.parseObject(o.toString());
            concurrentMapWordCounts.put(j.getString("Code"), j.getString("ProvinceName"));
        }
    }

    @AfterClass
    public static void onAfter() {
        mongoService.close();
    }


    @Test
    public void clearTables() {
        String sql = "select  tabname from syscat.tables where tabschema='DB2INST1' and tabname like 'QCC_%'\n";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        try {
            Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement();
            for (JSONObject object : list) {
                String s = "delete from " + object.getString("tabname");
                stmt.addBatch(s);
            }
            stmt.executeBatch();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void testDe(String url, String str) {
        JSONObject object = JSON.parseObject(str);
//        String url = object.getString("FullLink");
        for (Map.Entry<String, DeconstructService.DeconstructHandler> entry : DeconstructService.handlers.entrySet()) {
            if (HttpServerHandler.matches(url, entry.getKey())) {
                DeconstructService.DeconstructHandler handler = entry.getValue();
                ByteBuf buf = Unpooled.buffer();
                DefaultHttpHeaders headers = new DefaultHttpHeaders();
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url, buf, headers, headers);
                JSONObject od = (object.getJSONObject("OriginData"));
                if (S.eq(od.getString("Status"), "200")) {
                    handler.call(null, request, (JSON) od.get("Result"));
                }
            }
        }
    }

    public static String str = "{\n" +
            "        \"RegistCapi\": \"1000万人民币元\",\n" +
            "        \"CreditCode\": \"9111010859963405XW\",\n" +
            "        \"BelongOrg\": \"北京市工商行政管理局海淀分局\",\n" +
            "        \"Address\": \"北京市海淀区东北旺西路8号院35号楼5层501室\",\n" +
            "        \"EconKind\": \"有限责任公司(自然人投资或控股)\",\n" +
            "        \"UpdatedDate\": \"2018-06-19 12:00:00\",\n" +
            "        \"Employees\": [\n" +
            "            {\n" +
            "                \"Job\": \"执行董事\",\n" +
            "                \"Name\": \"程维\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"Job\": \"经理\",\n" +
            "                \"Name\": \"程维\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"Job\": \"监事\",\n" +
            "                \"Name\": \"吴睿\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"Name\": \"北京小桔科技有限公司\",\n" +
            "        \"InputDate\": \"2019-06-05 11:53:02\",\n" +
            "        \"StartDate\": \"2012-07-10 12:00:00\",\n" +
            "        \"Industry\": {\n" +
            "            \"Industry\": \"科学研究和技术服务业\",\n" +
            "            \"SubIndustryCode\": \"75\",\n" +
            "            \"IndustryCode\": \"M\",\n" +
            "            \"MiddleCategory\": \"其他科技推广服务业\",\n" +
            "            \"SmallCategoryCode\": \"7590\",\n" +
            "            \"SmallCategory\": \"其他科技推广服务业\",\n" +
            "            \"SubIndustry\": \"科技推广和应用服务业\",\n" +
            "            \"MiddleCategoryCode\": \"759\"\n" +
            "        },\n" +
            "        \"StockType\": null,\n" +
            "        \"ChangeRecords\": [\n" +
            "            {\n" +
            "                \"ProjectName\": \"经营范围\",\n" +
            "                \"ChangeDate\": \"2016-09-27 12:00:00\",\n" +
            "                \"AfterContent\": \"从事互联网文化活动；互联网信息服务；经营电信业务。技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口企业依法自主选择经营项目,开展经营活动；从事互联网文化活动、互联网信息服务、经营电信业务以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动；不得从事本市产业政策禁止和限制类项目的经营活动。\",\n" +
            "                \"BeforeContent\": \"从事互联网文化活动；第二类增值电信业务中的信息服务业务(仅限互联网信息服务)(互联网信息服务不含新闻、出版、教育、医疗保健、药品和医疗器械、电子公告服务)(电信与信息服务业务许可证有效期至2020年04月29日)；第二类增值电信业务中的信息服务业务(不含固定网电话信息服务和互联网信息服务)(增值电信业务经营许可证有效期至2020年04月29日)。技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口从事互联网文化活动以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动。\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"注册资本\",\n" +
            "                \"ChangeDate\": \"2015-09-21 12:00:00\",\n" +
            "                \"AfterContent\": \"1000万元（+864.51%）\",\n" +
            "                \"BeforeContent\": \"103.68万元\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"经营范围\",\n" +
            "                \"ChangeDate\": \"2015-09-21 12:00:00\",\n" +
            "                \"AfterContent\": \"从事互联网文化活动；第二类增值电信业务中的信息服务业务(仅限互联网信息服务)(互联网信息服务不含新闻、出版、教育、医疗保健、药品和医疗器械、电子公告服务)(电信与信息服务业务许可证有效期至2020年04月29日)；第二类增值电信业务中的信息服务业务(不含固定网电话信息服务和互联网信息服务)(增值电信业务经营许可证有效期至2020年04月29日)。技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口从事互联网文化活动以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动。\",\n" +
            "                \"BeforeContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口。依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动。\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"投资人\",\n" +
            "                \"ChangeDate\": \"2017-07-21 12:00:00\",\n" +
            "                \"AfterContent\": \"程维*,自然人股东;；王刚,自然人股东;；张博,自然人股东;；吴睿,自然人股东;；陈汀,自然人股东;\",\n" +
            "                \"BeforeContent\": \"程维*,自然人股东;；王刚,自然人股东;；陈汀,自然人股东;；张博,自然人股东;；吴睿,自然人股东;；徐涛,自然人股东;【退出】\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"注册资本\",\n" +
            "                \"ChangeDate\": \"2015-05-28 12:00:00\",\n" +
            "                \"AfterContent\": \"103.68万元（+3.68%）\",\n" +
            "                \"BeforeContent\": \"100万元\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"住所\",\n" +
            "                \"ChangeDate\": \"2014-06-30 12:00:00\",\n" +
            "                \"AfterContent\": \"北京市海淀区上地东路9号1幢5层北区2号\",\n" +
            "                \"BeforeContent\": \"北京市海淀区中关村大街11号9层980\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"经营范围\",\n" +
            "                \"ChangeDate\": \"2014-06-30 12:00:00\",\n" +
            "                \"AfterContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口。(依法须经批准的项目,经相关部门批准后方可开展经营活动)领取本执照后,应到市商务委备案。\",\n" +
            "                \"BeforeContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告。(依法须经批准的项目,经相关部门批准后方可开展经营活动)\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"经营范围\",\n" +
            "                \"ChangeDate\": \"2014-04-17 12:00:00\",\n" +
            "                \"AfterContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告。(依法须经批准的项目,经相关部门批准后方可开展经营活动)\",\n" +
            "                \"BeforeContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务。(未取得行政许可的项目除外)\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"投资人\",\n" +
            "                \"ChangeDate\": \"2015-05-28 12:00:00\",\n" +
            "                \"AfterContent\": \"程维*,自然人股东;；王刚,自然人股东;；陈汀,自然人股东;【新增】；张博,自然人股东;【新增】；吴睿,自然人股东;【新增】；徐涛,自然人股东;【新增】\",\n" +
            "                \"BeforeContent\": \"程维*,自然人股东;；王刚,自然人股东;\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"股东出资变更\",\n" +
            "                \"ChangeDate\": \"2012-10-25 12:00:00\",\n" +
            "                \"AfterContent\": \"程维*,自然人股东;；王刚,自然人股东;【新增】\",\n" +
            "                \"BeforeContent\": \"程维*,自然人股东;；吴睿,自然人股东;【退出】\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ProjectName\": \"住所\",\n" +
            "                \"ChangeDate\": \"2016-09-27 12:00:00\",\n" +
            "                \"AfterContent\": \"北京市海淀区东北旺西路8号院35号楼5层501室\",\n" +
            "                \"BeforeContent\": \"北京市海淀区上地东路9号1幢5层北区2号\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"CheckDate\": \"2017-12-11 12:00:00\",\n" +
            "        \"ContactInfo\": {\n" +
            "            \"Email\": null,\n" +
            "            \"WebSite\": [\n" +
            "                {\n" +
            "                    \"Url\": \"www.xiaojukeji.com\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"PhoneNumber\": \"010-62682929\"\n" +
            "        },\n" +
            "        \"Status\": \"开业\",\n" +
            "        \"No\": \"110108015068911\",\n" +
            "        \"OperName\": \"程维\",\n" +
            "        \"Branches\": [\n" +
            "            {\n" +
            "                \"CreditCode\": null,\n" +
            "                \"BelongOrg\": \"重庆市工商行政管理局南岸区分局经开区局\",\n" +
            "                \"OperName\": null,\n" +
            "                \"CompanyId\": \"8dc057af59e908b6a1c05d74de114134\",\n" +
            "                \"RegNo\": \"500902300083458\",\n" +
            "                \"Name\": \"北京小桔科技有限公司重庆分公司\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/4659626b1e5e43f1bcad8c268753216e.jpg\",\n" +
            "        \"OrgNo\": \"59963405-X\",\n" +
            "        \"OriginalName\": [\n" +
            "            {\n" +
            "                \"ChangeDate\": null,\n" +
            "                \"Name\": \"惠州市惠尔曼酒店管理有限公司\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"EndDate\": null,\n" +
            "        \"Province\": \"BJ\",\n" +
            "        \"TermStart\": \"2012-07-10 12:00:00\",\n" +
            "        \"TeamEnd\": \"2032-07-09 12:00:00\",\n" +
            "        \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n" +
            "        \"Partners\": [\n" +
            "            {\n" +
            "                \"ShoudDate\": null,\n" +
            "                \"CapiDate\": null,\n" +
            "                \"StockName\": \"程维\",\n" +
            "                \"StockType\": \"自然人股东\",\n" +
            "                \"StockPercent\": \"49.19%\",\n" +
            "                \"RealCapi\": null,\n" +
            "                \"InvestName\": null,\n" +
            "                \"ShouldCapi\": \"491.9\",\n" +
            "                \"InvestType\": \"货币\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ShoudDate\": null,\n" +
            "                \"CapiDate\": null,\n" +
            "                \"StockName\": \"王刚\",\n" +
            "                \"StockType\": \"自然人股东\",\n" +
            "                \"StockPercent\": \"48.22%\",\n" +
            "                \"RealCapi\": null,\n" +
            "                \"InvestName\": null,\n" +
            "                \"ShouldCapi\": \"482.25\",\n" +
            "                \"InvestType\": \"货币\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ShoudDate\": null,\n" +
            "                \"CapiDate\": null,\n" +
            "                \"StockName\": \"张博\",\n" +
            "                \"StockType\": \"自然人股东\",\n" +
            "                \"StockPercent\": \"1.55%\",\n" +
            "                \"RealCapi\": null,\n" +
            "                \"InvestName\": null,\n" +
            "                \"ShouldCapi\": \"15.53\",\n" +
            "                \"InvestType\": \"货币\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ShoudDate\": null,\n" +
            "                \"CapiDate\": null,\n" +
            "                \"StockName\": \"吴睿\",\n" +
            "                \"StockType\": \"自然人股东\",\n" +
            "                \"StockPercent\": \"0.72%\",\n" +
            "                \"RealCapi\": null,\n" +
            "                \"InvestName\": null,\n" +
            "                \"ShouldCapi\": \"7.23\",\n" +
            "                \"InvestType\": \"货币\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"ShoudDate\": null,\n" +
            "                \"CapiDate\": null,\n" +
            "                \"StockName\": \"陈汀\",\n" +
            "                \"StockType\": \"自然人股东\",\n" +
            "                \"StockPercent\": \"0.31%\",\n" +
            "                \"RealCapi\": null,\n" +
            "                \"InvestName\": null,\n" +
            "                \"ShouldCapi\": \"3.09\",\n" +
            "                \"InvestType\": \"货币\"\n" +
            "            }\n" +
            "        ],\n" +
            "        \"Scope\": \"技术开发、技术咨询、技术服务、技术推广;基础软件服务;应用软件服务;设计、制作、代理、发布广告;软件开发;销售自行开发后的产品;企业管理咨询;计算机系统服务;组织文化艺术交流活动(不含营业性演出);公共关系服务;企业策划、设计;会议服务;市场调查;货物进出口、技术进出口、代理进出口;从事互联网文化活动;互联网信息服务;经营电信业务。(企业依法自主选择经营项目,开展经营活动;从事互联网文化活动、互联网信息服务、经营电信业务以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动;不得从事本市产业政策禁止和限制类项目的经营活动。)\",\n" +
            "        \"StockNumber\": null,\n" +
            "        \"IsOnStock\": \"0\"\n" +
            "    }";

    @Test
    public void testSingleFile() throws FileNotFoundException, InterruptedException {
        deconstructService.onDeconstructRequest("1", "2", new FileInputStream("C:\\Users\\DELL\\Desktop\\jbxx-load-qccfecd2634-38ec-4439-b8fd-c63bb95a65db.zip"));
    }


    public JSONObject qccGet(String url) throws UnsupportedEncodingException {
        url = "http://localhost:8081" + url;
        if (url.contains("?")) {
            int idex = url.indexOf("?");
            url = url.substring(0, idex) + "?" + URLUtil.encode(url.substring(idex + 1));
        }
        String str = HttpUtil.get(url);
        return JSONObject.parseObject(str);
    }


    @Test
    public void filxTest() {
        String sql = "select name,province from  QCC_DETAILS";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        Class clazz = null;
        try {
            JSON json = JSON.parseObject(TestMongo.str);
            clazz = Class.forName("com.beeasy.zed.DeconstructService");
            Method method = clazz.getDeclaredMethod("GetDetailsByName", ChannelHandlerContext.class, FullHttpRequest.class, JSON.class);
            method.setAccessible(true);
            for (int i = 0; i < 20; i++) {
                Object obj = method.invoke(clazz.newInstance(), null, new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/ECIV4/GetDetailsByName.json?keyword=" + list.get(i), Unpooled.buffer(), new DefaultHttpHeaders(), new DefaultHttpHeaders()), json);
                System.out.println(obj + "xxxxxxxxxx");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateProvinde() {
//        File file = new File("D:\\mynsh\\mynsh\\hz-ms-zed\\src\\main\\resources\\fixed.txt");
        String sql = "select name,province from  QCC_DETAILS";
        List<JSONObject> list = sqlManager.execute(new SQLReady(sql), JSONObject.class);
        System.out.println(App.concurrentMapWordCounts);
        for (int i = 371; i < list.size(); i++) {
            if (null != list.get(i).getString("province") && null != App.concurrentMapWordCounts.get(list.get(i).getString("province")) && !App.concurrentMapWordCounts.get(list.get(i).getString("province")).equals("")) {
                String updateSql = "update QCC_DETAILS set province = '"+App.concurrentMapWordCounts.get(list.get(i).getString("province")) +"'  where  name = '"+ list.get(i).getString("name")+ "'";
                sqlManager.executeUpdate(new SQLReady(updateSql));
            }
        }
    }


    String j = "{\n" +
            "    \"Partners\": [\n" +
            "      {\n" +
            "        \"StockName\": \"程维\",\n" +
            "        \"StockType\": \"自然人股东\",\n" +
            "        \"StockPercent\": \"49.19%\",\n" +
            "        \"ShouldCapi\": \"491.9\",\n" +
            "        \"ShoudDate\": null,\n" +
            "        \"InvestType\": \"货币\",\n" +
            "        \"InvestName\": null,\n" +
            "        \"RealCapi\": null,\n" +
            "        \"CapiDate\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"StockName\": \"王刚\",\n" +
            "        \"StockType\": \"自然人股东\",\n" +
            "        \"StockPercent\": \"48.22%\",\n" +
            "        \"ShouldCapi\": \"482.25\",\n" +
            "        \"ShoudDate\": null,\n" +
            "        \"InvestType\": \"货币\",\n" +
            "        \"InvestName\": null,\n" +
            "        \"RealCapi\": null,\n" +
            "        \"CapiDate\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"StockName\": \"张博\",\n" +
            "        \"StockType\": \"自然人股东\",\n" +
            "        \"StockPercent\": \"1.55%\",\n" +
            "        \"ShouldCapi\": \"15.53\",\n" +
            "        \"ShoudDate\": null,\n" +
            "        \"InvestType\": \"货币\",\n" +
            "        \"InvestName\": null,\n" +
            "        \"RealCapi\": null,\n" +
            "        \"CapiDate\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"StockName\": \"吴睿\",\n" +
            "        \"StockType\": \"自然人股东\",\n" +
            "        \"StockPercent\": \"0.72%\",\n" +
            "        \"ShouldCapi\": \"7.23\",\n" +
            "        \"ShoudDate\": null,\n" +
            "        \"InvestType\": \"货币\",\n" +
            "        \"InvestName\": null,\n" +
            "        \"RealCapi\": null,\n" +
            "        \"CapiDate\": null\n" +
            "      },\n" +
            "      {\n" +
            "        \"StockName\": \"陈汀\",\n" +
            "        \"StockType\": \"自然人股东\",\n" +
            "        \"StockPercent\": \"0.31%\",\n" +
            "        \"ShouldCapi\": \"3.09\",\n" +
            "        \"ShoudDate\": null,\n" +
            "        \"InvestType\": \"货币\",\n" +
            "        \"InvestName\": null,\n" +
            "        \"RealCapi\": null,\n" +
            "        \"CapiDate\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"Employees\": [\n" +
            "      {\n" +
            "        \"Name\": \"程维\",\n" +
            "        \"Job\": \"执行董事\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"Name\": \"程维\",\n" +
            "        \"Job\": \"经理\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"Name\": \"吴睿\",\n" +
            "        \"Job\": \"监事\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"Branches\": [\n" +
            "      {\n" +
            "        \"CompanyId\": \"8dc057af59e908b6a1c05d74de114134\",\n" +
            "        \"RegNo\": \"500902300083458\",\n" +
            "        \"Name\": \"北京小桔科技有限公司重庆分公司\",\n" +
            "        \"BelongOrg\": \"重庆市工商行政管理局南岸区分局经开区局\",\n" +
            "        \"CreditCode\": null,\n" +
            "        \"OperName\": null\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ChangeRecords\": [\n" +
            "      {\n" +
            "        \"ProjectName\": \"住所\",\n" +
            "        \"BeforeContent\": \"北京市海淀区上地东路9号1幢5层北区2号\",\n" +
            "        \"AfterContent\": \"北京市海淀区东北旺西路8号院35号楼5层501室\",\n" +
            "        \"ChangeDate\": \"2016-09-27T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"经营范围\",\n" +
            "        \"BeforeContent\": \"从事互联网文化活动；第二类增值电信业务中的信息服务业务(仅限互联网信息服务)(互联网信息服务不含新闻、出版、教育、医疗保健、药品和医疗器械、电子公告服务)(电信与信息服务业务许可证有效期至2020年04月29日)；第二类增值电信业务中的信息服务业务(不含固定网电话信息服务和互联网信息服务)(增值电信业务经营许可证有效期至2020年04月29日)。技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口从事互联网文化活动以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动。\",\n" +
            "        \"AfterContent\": \"从事互联网文化活动；互联网信息服务；经营电信业务。技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口企业依法自主选择经营项目,开展经营活动；从事互联网文化活动、互联网信息服务、经营电信业务以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动；不得从事本市产业政策禁止和限制类项目的经营活动。\",\n" +
            "        \"ChangeDate\": \"2016-09-27T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"注册资本\",\n" +
            "        \"BeforeContent\": \"103.68万元\",\n" +
            "        \"AfterContent\": \"1000万元（+864.51%）\",\n" +
            "        \"ChangeDate\": \"2015-09-21T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"经营范围\",\n" +
            "        \"BeforeContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口。依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动。\",\n" +
            "        \"AfterContent\": \"从事互联网文化活动；第二类增值电信业务中的信息服务业务(仅限互联网信息服务)(互联网信息服务不含新闻、出版、教育、医疗保健、药品和医疗器械、电子公告服务)(电信与信息服务业务许可证有效期至2020年04月29日)；第二类增值电信业务中的信息服务业务(不含固定网电话信息服务和互联网信息服务)(增值电信业务经营许可证有效期至2020年04月29日)。技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口从事互联网文化活动以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动。\",\n" +
            "        \"ChangeDate\": \"2015-09-21T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"投资人\",\n" +
            "        \"BeforeContent\": \"程维*,自然人股东;；王刚,自然人股东;；陈汀,自然人股东;；张博,自然人股东;；吴睿,自然人股东;；徐涛,自然人股东;【退出】\",\n" +
            "        \"AfterContent\": \"程维*,自然人股东;；王刚,自然人股东;；张博,自然人股东;；吴睿,自然人股东;；陈汀,自然人股东;\",\n" +
            "        \"ChangeDate\": \"2017-07-21T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"注册资本\",\n" +
            "        \"BeforeContent\": \"100万元\",\n" +
            "        \"AfterContent\": \"103.68万元（+3.68%）\",\n" +
            "        \"ChangeDate\": \"2015-05-28T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"住所\",\n" +
            "        \"BeforeContent\": \"北京市海淀区中关村大街11号9层980\",\n" +
            "        \"AfterContent\": \"北京市海淀区上地东路9号1幢5层北区2号\",\n" +
            "        \"ChangeDate\": \"2014-06-30T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"经营范围\",\n" +
            "        \"BeforeContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告。(依法须经批准的项目,经相关部门批准后方可开展经营活动)\",\n" +
            "        \"AfterContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告；软件开发；销售自行开发后的产品；企业管理咨询；计算机系统服务；组织文化艺术交流活动(不含营业性演出)；公共关系服务；企业策划、设计；会议服务；市场调查；货物进出口、技术进出口、代理进出口。(依法须经批准的项目,经相关部门批准后方可开展经营活动)领取本执照后,应到市商务委备案。\",\n" +
            "        \"ChangeDate\": \"2014-06-30T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"经营范围\",\n" +
            "        \"BeforeContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务。(未取得行政许可的项目除外)\",\n" +
            "        \"AfterContent\": \"技术开发、技术咨询、技术服务、技术推广；基础软件服务；应用软件服务；设计、制作、代理、发布广告。(依法须经批准的项目,经相关部门批准后方可开展经营活动)\",\n" +
            "        \"ChangeDate\": \"2014-04-17T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"投资人\",\n" +
            "        \"BeforeContent\": \"程维*,自然人股东;；王刚,自然人股东;\",\n" +
            "        \"AfterContent\": \"程维*,自然人股东;；王刚,自然人股东;；陈汀,自然人股东;【新增】；张博,自然人股东;【新增】；吴睿,自然人股东;【新增】；徐涛,自然人股东;【新增】\",\n" +
            "        \"ChangeDate\": \"2015-05-28T00:00:00+08:00\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"ProjectName\": \"股东出资变更\",\n" +
            "        \"BeforeContent\": \"程维*,自然人股东;；吴睿,自然人股东;【退出】\",\n" +
            "        \"AfterContent\": \"程维*,自然人股东;；王刚,自然人股东;【新增】\",\n" +
            "        \"ChangeDate\": \"2012-10-25T00:00:00+08:00\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"ContactInfo\": {\n" +
            "      \"WebSite\": [\n" +
            "        {\n" +
            "          \"Name\": null,\n" +
            "          \"Url\": \"www.xiaojukeji.com\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"PhoneNumber\": \"010-62682929\",\n" +
            "      \"Email\": null\n" +
            "    },\n" +
            "    \"Industry\": {\n" +
            "      \"IndustryCode\": \"M\",\n" +
            "      \"Industry\": \"科学研究和技术服务业\",\n" +
            "      \"SubIndustryCode\": \"75\",\n" +
            "      \"SubIndustry\": \"科技推广和应用服务业\",\n" +
            "      \"MiddleCategoryCode\": \"759\",\n" +
            "      \"MiddleCategory\": \"其他科技推广服务业\",\n" +
            "      \"SmallCategoryCode\": \"7590\",\n" +
            "      \"SmallCategory\": \"其他科技推广服务业\"\n" +
            "    },\n" +
            "    \"KeyNo\": \"4659626b1e5e43f1bcad8c268753216e\",\n" +
            "    \"Name\": \"北京小桔科技有限公司\",\n" +
            "    \"No\": \"110108015068911\",\n" +
            "    \"BelongOrg\": \"北京市工商行政管理局海淀分局\",\n" +
            "    \"OperName\": \"程维\",\n" +
            "    \"StartDate\": \"2012-07-10T00:00:00\",\n" +
            "    \"EndDate\": null,\n" +
            "    \"Status\": \"开业\",\n" +
            "    \"Province\": \"BJ\",\n" +
            "    \"UpdatedDate\": \"2018-06-19T00:15:47\",\n" +
            "    \"CreditCode\": \"9111010859963405XW\",\n" +
            "    \"RegistCapi\": \"1000万人民币元\",\n" +
            "    \"EconKind\": \"有限责任公司(自然人投资或控股)\",\n" +
            "    \"Address\": \"北京市海淀区东北旺西路8号院35号楼5层501室\",\n" +
            "    \"Scope\": \"技术开发、技术咨询、技术服务、技术推广;基础软件服务;应用软件服务;设计、制作、代理、发布广告;软件开发;销售自行开发后的产品;企业管理咨询;计算机系统服务;组织文化艺术交流活动(不含营业性演出);公共关系服务;企业策划、设计;会议服务;市场调查;货物进出口、技术进出口、代理进出口;从事互联网文化活动;互联网信息服务;经营电信业务。(企业依法自主选择经营项目,开展经营活动;从事互联网文化活动、互联网信息服务、经营电信业务以及依法须经批准的项目,经相关部门批准后依批准的内容开展经营活动;不得从事本市产业政策禁止和限制类项目的经营活动。)\",\n" +
            "    \"TermStart\": \"2012-07-10T00:00:00\",\n" +
            "    \"TeamEnd\": \"2032-07-09T00:00:00\",\n" +
            "    \"CheckDate\": \"2017-12-11T00:00:00\",\n" +
            "    \"OrgNo\": \"59963405-X\",\n" +
            "    \"IsOnStock\": \"0\",\n" +
            "    \"StockNumber\": null,\n" +
            "    \"StockType\": null,\n" +
            "    \"OriginalName\": [],\n" +
            "    \"ImageUrl\": \"https://co-image.qichacha.com/CompanyImage/4659626b1e5e43f1bcad8c268753216e.jpg\"\n" +
            "  }";

    @Test
    public void testSingleJson() throws NoSuchMethodException, SQLException {
        String path = "/ECIV4/GetDetailsByName";
        String compName = "北京小桔科技有限公司重庆分公司";
        JSONObject json = JSONObject.parseObject(j.toString());
        DeconstructService.DeconstructHandler handler = deconstructService.handlers.get(path);
        ByteBuf buf = Unpooled.buffer();
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://www.baidu.com" + path + "?keyword=" + compName, buf, headers, headers);

        handler.call(null, request, json);
        try {

            deconstructService.deconstructStep3(deconstructService.readySqls);
        }catch (Exception e){

            e.printStackTrace();
        }

    }



}
