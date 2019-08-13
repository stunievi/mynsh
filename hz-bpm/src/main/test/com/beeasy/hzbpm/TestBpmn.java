package com.beeasy.hzbpm;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import com.beeasy.hzbpm.bean.Notice;
import com.beeasy.hzbpm.bpm.Bpm;
import com.github.llyb120.nami.core.Nami;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

public class TestBpmn {

    @Test
    public void test() throws FileNotFoundException {
        String str = IoUtil.read(new FileReader("/Users/bin/work/win/hznsh/hz-Bpm/src/main/resources/diagram (14).bpmn"));
        String json = IoUtil.read(new FileReader("/Users/bin/work/win/hznsh/hz-bpm/src/main/resources/test.json"));
        ;
        Bpm bpm = new Bpm();
        Node node = bpm.getStartNode(str);
        Assert.notNull(node);
        String id = bpm.getNodeAttribute(node, "id");
        Assert.notBlank(id);
//        bpm.getUsersFromNode(node, JSON.parseObject(json, new TypeReference<List<NodeExt>>(){}));
    }


    @Test
    public void test2() throws InterruptedException {
        Nami.dev();;
        Notice.sendSystem(Arrays.asList("522"), "oh shit");

        Thread.sleep(10000);
    }
}
