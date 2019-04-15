package com.beeasy.zed;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import org.apache.activemq.BlobMessage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.S;

import javax.jms.JMSException;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestMQ {

    private Lock lock = new ReentrantLock();
    @BeforeClass
    public static void onBeforeClass(){
        MQService.initMQ();
    }

    public void test(){

//        RedissonClient redis = Redisson.create();
//        Config config = redis.getConfig();

    }
    @Test
    public void testSend(){
        AtomicBoolean runing = new AtomicBoolean(true);
        MQService.setOnMessageListener(message -> {
            if(message instanceof TextMessage){
                Assert.assertTrue(S.notEmpty(String.valueOf(message)));
            } else {
                Assert.assertTrue(false);
            }
            runing.set(false);
        });
        MQService.sendMessage("rilegou");
        //等待消息
        while(runing.get()){

        }
    }

    @Test
    public void testSendFile(){
        CountDownLatch cl=new CountDownLatch(1);
        MQService.setOnMessageListener(message -> {
            if(message instanceof TextMessage){
                Assert.assertTrue(S.notEmpty(String.valueOf(message)));
            } else if(message instanceof StreamMessage){
                try {
                    StringBuilder builder = new StringBuilder();
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while((len = (((StreamMessage) message).readBytes(bytes))) > 0){
                        builder.append(new String(Arrays.copyOfRange(bytes, 0, len)));
                    }
                    Assert.assertTrue(S.notEmpty(builder.toString()));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            } else if(message instanceof BlobMessage){
                try (
                    InputStream is = ((BlobMessage) message).getInputStream();
                    ){
                    StringBuilder builder = new StringBuilder();
                    byte[] bytes = new byte[1024];
                    int len = -1;
                    while((len = is.read(bytes)) > 0){
                        builder.append(new String(Arrays.copyOfRange(bytes, 0, len)));
                    }
                    Assert.assertTrue(S.notEmpty(builder.toString()));
                } catch (JMSException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cl.countDown();
        });
        MQService.sendMessage(new File("D:\\work\\hznsh\\hz-ms-zed\\src\\main\\resources\\zed.json"));
        //等待消息
        try {
            cl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testReceive() throws InterruptedException {
        CountDownLatch cl=new CountDownLatch(1);

            MQService.setOnMessageListener(message -> {
                if(message instanceof TextMessage){
                    Assert.assertTrue(S.notEmpty(String.valueOf(message)));
                } else {
                    Assert.assertTrue(false);
                }
            });

        cl.await();
    }
}
