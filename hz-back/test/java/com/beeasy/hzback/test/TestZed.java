package com.beeasy.hzback.test;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.lib.zed.Result;
import com.beeasy.hzback.lib.zed.Zed;
import com.beeasy.hzback.lib.zed.exception.ErrorWhereFieldsException;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestZed {

    @Autowired
    Zed zed;

    static Integer id = -1;

    @Test
    public void test1_ErrorJSON(){
        String str = "{\n" +
                "\"method\":\"post\",\n" +
                "\"User\":{\n" +
                "\t\"username\":\"aaa,\n" +
                "}\n" +
                "}";
        try {
            zed.parseSingle(str);
        } catch (Exception e) {
            Assert.assertNotEquals(e,null);
        }
    }

    @Test
    public void test2_AddSuccess(){
        String str = "{\n" +
                "\"method\":\"post\",\n" +
                "\"User\":{\n" +
                "\t\"username\":\"aaa\",\n" +
                "\t\"password\":\"111\"\n" +
                "}\n" +
                "}";

        try {
            Map<?,?> map = zed.parseSingle(str);
            Assert.assertTrue(map.size() > 0);
            Assert.assertTrue(map.containsKey("User"));
            Assert.assertTrue((Integer)map.get("User") > 0);

            id = (Integer)map.get("User");
        } catch (Exception e) {
            Assert.assertEquals(e,null);
        }
    }

    @Test
    public void test3_AddFailed(){
        String str = "{\n" +
                "\"method\":\"post\",\n" +
                "\"User\":{\n" +
                "\t\"username\":\"aaa\"\n" +
                "}\n" +
                "}";
        try {
            zed.parseSingle(str);
        } catch (Exception e) {
            Assert.assertNotEquals(e,null);
        }
    }

    @Test
    public void test4_deleteSuccess() {
        Assert.assertTrue(id > 0);
        String testStr = "{\n" +
                "\t\"method\":\"delete\",\n" +
                "\t\"User\":{\n" +
                "\t\t\"id\":\""+id+"\"\n" +
                "\t}\n" +
                "}";


        try {
            zed.parseSingle(testStr);
        } catch (Exception e) {
            Assert.assertEquals(e,null);
        }
    }


    @Test
    public void test5_permissionError(){
        String testStr = "{\n" +
                "\t\"method\":\"delete\",\n" +
                "\t\"User\":{\n" +
                "\t\t\"id\":\""+id+"\"\n" +
                "\t}\n" +
                "}";


        try {
            zed.parseSingle(testStr,"UNKNOWN");
        } catch (Exception e) {
            Assert.assertNotEquals(e,null);
        }
    }

    @Test
    public void test6_noGetPermission(){
        String testStr = "{\n" +
                "\t\"method\":\"get\",\n" +
                "\t\"User\":{\n" +
                "\t}\n" +
                "}";


        try {
            zed.parseSingle(testStr,"TEST");
        } catch (Exception e) {
            Assert.assertNotEquals(e,null);
        }
    }

    @Test
    public void test7_hasGetPermission(){
        String testStr = "{\n" +
                "\t\"method\":\"get\",\n" +
                "\t\"User\":{\n" +
                "\t}\n" +
                "}";

        try {
            Map<?,?> ret = zed.parseSingle(testStr,"TEST2");
            Assert.assertTrue(ret.size() > 0);
            JSONObject o = (JSONObject) ret.get("User");
            Assert.assertNotNull(o);
            Assert.assertTrue(!o.containsKey("password"));
        } catch (Exception e) {
            Assert.assertEquals(e,null);
        }
    }


    @Test
    public void test8_getUniqueWherePermission(){
        String testStr1 = "{\n" +
                "\t\"method\":\"get\",\n" +
                "\t\"User\":{\n" +
                "\t}\n" +
                "}";

        try {
            Map<?,?> ret = zed.parseSingle(testStr1,"TEST3");
        } catch (ErrorWhereFieldsException e) {
            Assert.assertNotNull(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String testStr2 = "{\n" +
                "\t\"method\":\"get\",\n" +
                "\t\"User\":{\n" +
                "\"$where\":{\"username\":1}" +
                "\t}\n" +
                "}";
        try {
            Map<?,?> ret = zed.parseSingle(testStr2,"TEST3");
        } catch (ErrorWhereFieldsException e) {
            Assert.assertEquals(e,null);
        } catch (Exception e) {
            Assert.assertEquals(e,null);
        }
    }





}
