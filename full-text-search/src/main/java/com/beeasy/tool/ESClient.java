package com.beeasy.tool;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.osgl.util.C;
import org.osgl.util.S;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteDateUseDateFormat;

public class ESClient {

    private String url   = "";
    private String index = "";
    private String type  = "";

    public ESClient(String url, String index, String type) {
        this.url = url;
        this.index = index;
        this.type = type;
    }


    //检查是否存在某个索引
//    public  boolean indexExists(String index){
//        try {
//            String cnt = get("/_cat/indices?v");
//            System.out.println(cnt);
//            return Arrays.stream(cnt.split("\n"))
//                .anyMatch(line -> {
//                    String[] items = line.split("\\s+");
//                    return items.length > 0 && S.eq(items[2], index);
//                });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }


    private String buildUrl(String path) {
        return url + path;
    }

    private String get(String url) throws IOException {
        return OkHttpUtil.request("get", buildUrl(url), null, null, null);
    }

    private String get(String url, Map json) throws IOException {
        return OkHttpUtil.request("get", buildUrl(url), null, null, JSON.toJSONString(json));
    }

    private String put(String url, String json) throws IOException {
        return OkHttpUtil.request("put", buildUrl(url), null, null, json);
    }

    private String put(String url, Map json) throws IOException {
        return OkHttpUtil.request("put", buildUrl(url), null, null, JSON.toJSONString(json));
    }

    private String post(String url, String json) throws IOException {
        return OkHttpUtil.request("post", buildUrl(url), null, null, json);
    }

    private String post(String url, Map map) throws IOException {
        return post(url, JSON.toJSONString(map));
    }

    private String delete(String url) throws IOException {
        return OkHttpUtil.request("delete", buildUrl(url), null, null, null);
    }


    public void deleteIndex() throws IOException {
        delete("/" + index);
    }

    public void createIndex() throws IOException {
        put("/" + index, (C.newMap(
            "mappings", C.newMap(
                type, C.newMap(
                    "dynamic", false,
                    "properties", C.newMap(
                        "content", C.newMap(
                            "type", "text",
                            "fields", C.newMap(
                                "cn", C.newMap(
                                    "type", "text",
                                    "analyzer", "ik_smart"
                                ),
                                "en", C.newMap(
                                    "type", "text",
                                    "analyzer", "english"
                                )
                            )
                        ),
                        "title", C.newMap(
                            "type", "text",
                            "fields", C.newMap(
                                "cn", C.newMap(
                                    "type", "text",
                                    "analyzer", "ik_smart"
                                ),
                                "en", C.newMap(
                                    "type", "text",
                                    "analyzer", "english"
                                )
                            )
                        ),
                        "uid", C.newMap("type", "long"),
                        "fid", C.newMap("type", "long"),
                        "createTime", C.newMap("type", "date"),
                        "modifyTime", C.newMap("type", "date")
                    )
                )
            )
        )));
    }

    public boolean indexExists() {
        try {
            get(S.fmt("/%s", index));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public JSONObject updateDocument(Document document) throws IOException {
        String json = JSON.toJSONString(document);
        String str = put(S.fmt("/%s/%s/%d", index, type, document.getFid()), json);
        return JSON.parseObject(str);
    }

    public void deleteDocument(long fid) throws IOException {
        delete(S.fmt("/%s/%s/%d", index, type, fid));
    }

    public JSONObject search(SearchParameter parameter) throws IOException {
        List must = C.newList(
            C.newMap(
                "term", C.newMap(
                    "uid", parameter.getUid()
                )
            )
        );
        LinkedHashSet should = new LinkedHashSet();
        if(S.notBlank(parameter.getKeyword())){
            should.add(
                C.newMap(
                    "multi_match", C.newMap(
                        "query",  parameter.getKeyword(),
                        "fields", C.newList("title", "content")
                    )
                )
            );
        }

        //创建开始时间
        Map createTimeFilter = C.newMap(
            "match", C.newMap(
                "lte", 0,
                "gte", 0
            )
        );
        if(null != parameter.getCreateMin()){
            ((Map)createTimeFilter.get("match")).put("gte", parameter.getCreateMin().getTime());
            should.add(createTimeFilter);
        }
        if(null != parameter.getCreateMax()){
            ((Map)createTimeFilter.get("match")).put("lte", parameter.getCreateMax().getTime());
            should.add(createTimeFilter);
        }
        //最后修改时间
        Map modifyTimeFilter = C.newMap(
            "match", C.newMap(
                "lte", 0,
                "gte", 0
            )
        );
        if(null != parameter.getModifyMin()){
            ((Map)modifyTimeFilter.get("match")).put("gte", parameter.getModifyMin().getTime());
            should.add(modifyTimeFilter);
        }
        if(null != parameter.getModifyMax()){
            ((Map)createTimeFilter.get("match")).put("lte", parameter.getModifyMax().getTime());
            should.add(modifyTimeFilter);
        }

        Map params = C.newMap(
            "query", C.newMap(
                "bool", C.newMap(
                    "must", must,
                    "should", should
                )
            ),
            "_source", C.newList("fid", "uid", "title", "modifyTime", "createTime"),
            "highlight", C.newMap(
                "fields", C.newMap(
                    "content", C.newMap()
                )
            )
        );

        //排序
        if(S.notBlank(parameter.getSortField()) && S.notBlank(parameter.getSortType())){
            params.put("sort", C.newList(
                C.newMap(
                    parameter.getSortField(), parameter.getSortType()
                )
            ));
        }

        //分页
        params.put("from", (parameter.getPage() - 1) * parameter.getSize());
        params.put("size", parameter.getSize());

        String json = post(S.fmt("/%s/%s/_search", index, type), params);

        return JSON.parseObject(json);
    }


}
