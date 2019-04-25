package com.beeasy.zed;

import org.apache.activemq.BlobMessage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.util.IO;
import org.osgl.util.S;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.*;
import java.util.concurrent.CountDownLatch;

public class TestMQ {

    @BeforeClass
    public static void onBeforeClass(){
        MQService.init();
    }

    public void test(){

//        RedissonClient redis = Redisson.create();
//        Config config = redis.getConfig();

    }
    @Test
    public void testSend() throws InterruptedException {
        CountDownLatch cl = new CountDownLatch(1);
        MQService.listenMessage("queue", "fuck", message -> {
            if(message instanceof TextMessage){
                Assert.assertTrue(S.notEmpty(String.valueOf(message)));
            } else if(message instanceof BlobMessage){
                try {
                    IO.copy(((BlobMessage) message).getInputStream(), new FileOutputStream(new File("d:/ttt.zip")));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                int c= 1;
//                Assert.assertTrue(false);
            }
//            cl.countDown();
        });
        MQService.sendMessage("queue", "my_msg",  new File("D:\\xp\\vmware.log"));
        cl.await();
    }

//    @Test
//    public void testSendFile(){
//        CountDownLatch cl=new CountDownLatch(1);
//        MQService.setOnMessageListener(message -> {
//            if(message instanceof TextMessage){
//                Assert.assertTrue(S.notEmpty(String.valueOf(message)));
//            } else if(message instanceof StreamMessage){
//                try {
//                    StringBuilder builder = new StringBuilder();
//                    byte[] bytes = new byte[1024];
//                    int len = -1;
//                    while((len = (((StreamMessage) message).readBytes(bytes))) > 0){
//                        builder.append(new String(Arrays.copyOfRange(bytes, 0, len)));
//                    }
//                    Assert.assertTrue(S.notEmpty(builder.toString()));
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                }
//            } else if(message instanceof BlobMessage){
//                try (
//                    InputStream is = ((BlobMessage) message).getInputStream();
//                    ){
//                    StringBuilder builder = new StringBuilder();
//                    byte[] bytes = new byte[1024];
//                    int len = -1;
//                    while((len = is.read(bytes)) > 0){
//                        builder.append(new String(Arrays.copyOfRange(bytes, 0, len)));
//                    }
//                    Assert.assertTrue(S.notEmpty(builder.toString()));
//                } catch (JMSException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            cl.countDown();
//        });
//        MQService.sendMessage("topic","", new File("D:\\work\\hznsh\\hz-ms-zed\\src\\main\\resources\\zed.json"));
//        //等待消息
//        try {
//            cl.await();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Test
//    public void testReceive() throws InterruptedException {
//        CountDownLatch cl=new CountDownLatch(1);
//
//            MQService.setOnMessageListener(message -> {
//                if(message instanceof TextMessage){
//                    Assert.assertTrue(S.notEmpty(String.valueOf(message)));
//                } else {
//                    Assert.assertTrue(false);
//                }
//            });
//
//        cl.await();
//    }
}
