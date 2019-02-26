package com.beeasy.tool.test;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.tool.Document;
import com.beeasy.tool.ESClient;
import static  org.junit.Assert.*;

import com.beeasy.tool.SearchParameter;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class TestES {

    /**
     * 测试索引
     */
    @Test
    public void testIndex(){
        ESClient es = esClient();
        try{
            if(es.indexExists()){
                es.deleteIndex();
            }
            es.createIndex();
        }
        catch (Exception e){
            assertNull(e);
        }
    }


    @Test
    public void testCreateDocument(){
        for(int i =0; i < 10; i ++){
            Document document = new Document();
            document.setFid(i);
            document.setUid(1);
            document.setTitle("日");
            document.setContent("日");
            document.setCreateTime(new Date());
            document.setModifyTime(new Date());

            try{
                //删除文档
                esClient().deleteDocument(document.getFid());
                continue;
            }
            catch (IOException e){

            }

            try {
                //创建文档
                JSONObject json = esClient().updateDocument(document);
                String id = json.getString("_id");
                assertNotNull(id);

                //修改文档
                document.setContent("了");
                json = esClient().updateDocument(document);
                id = json.getString("_id");
                assertNotNull(id);


            } catch (IOException e) {
                e.printStackTrace();
                assertNull(e);
            }
        }

        try {
            //全文索引
            SearchParameter parameter = new SearchParameter();
            parameter.setKeyword("了");
            parameter.setUid(1);
            parameter.setPage(1);
            parameter.setSize(2);
            JSONObject ret = esClient().search(parameter);

        } catch (IOException e) {
            e.printStackTrace();
            assertNull(e);
        }


    }


    private ESClient esClient(){
        ESClient es = new ESClient("http://47.94.97.138/es", "aaa", "document");
        return es;
    }
}
