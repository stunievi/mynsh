package com.beeasy.hzback.test;

import com.beeasy.hzback.lib.zed.Result;
import com.beeasy.hzback.lib.zed.Zed;
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



}
