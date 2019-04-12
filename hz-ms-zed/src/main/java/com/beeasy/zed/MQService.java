package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;
import org.apache.activemq.*;

import javax.jms.*;
import javax.jms.Message;
import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MQService {

    private static ExecutorService executorService = Executors.newFixedThreadPool(8);
    private static ActiveMQConnection connection = null;
    private static ActiveMQSession session = null;
    private static MessageConsumer consumer = null;
    private static MessageProducer producer = null;
    private static Queue queue = null;
    private static Lock lock = new ReentrantLock();
    private static Vector<MQMessageListener> vector = new Vector<>();

    public static void initMQ(){
        ThreadUtil.execAsync(() -> {
            lock.lock();
            try{
                //1、创建工厂连接对象，需要制定ip和端口号
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://47.94.97.138:8011?jms.blobTransferPolicy.uploadUrl=http://47.94.97.138:8012/fileserver/");
//                connectionFactory.setUserName("admin");
//                connectionFactory.setPassword("admin");
                //2、使用连接工厂创建一个连接对象
                connection = (ActiveMQConnection) connectionFactory.createConnection();
                //3、开启连接
                connection.start();
                //4、使用连接对象创建会话（session）对象
                session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //5、使用会话对象创建目标对象，包含queue和topic（一对一和一对多）
                Topic queue = session.createTopic("my_msg");
                //6、使用会话对象创建生产者对象
                consumer = session.createConsumer(queue);
                producer = session.createProducer(queue);
                //7、向consumer对象中设置一个messageListener对象，用来接收消息
                consumer.setMessageListener(new MessageListener() {

                    @Override
                    public void onMessage(Message message) {

                        for (MQMessageListener mqMessageListener : vector) {
                            executorService.execute(() -> {
                                mqMessageListener.call(message);
                            });
                        }
                        // TODO Auto-generated method stub
                        if(message instanceof TextMessage){
//                            TextMessage textMessage = (TextMessage)message;
//                            try {
//                                System.out.println(textMessage.getText());
//                            } catch (JMSException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
                        }
                    }
                });


            }
            catch (Exception ee){
                ee.printStackTrace();
                try {
                    consumer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                try {
                    producer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }

                ThreadUtil.sleep(300);
                initMQ();
            } finally {
                lock.unlock();
            }
        });
    }

    public static void sendMessage(Object message){
        sendMessage(message, 300, 5);
    }

    public static void sendMessage(Object message, final int delay, int retryTimes){
        ThreadUtil.execAsync(() -> {
            lock.lock();
            try{
                if(message instanceof String){
                    TextMessage msg = session.createTextMessage((String) message);
                    producer.send(msg);
                } else if (message instanceof File){
                    BytesMessage bytesMessage = session.createBytesMessage();
                    BlobMessage blobMessage = session.createBlobMessage((File) message);
                    StreamMessage streamMessage = session.createStreamMessage();
                    try (FileInputStream is = new FileInputStream((File) message)){
                        byte[] buf = new byte[1024];
                        int len = -1;
                        while((len = is.read(buf)) > 0){
                            streamMessage.writeBytes(buf, 0, len);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    producer.send(blobMessage);
                }
                lock.unlock();
            } catch (Exception e){
                e.printStackTrace();
                lock.unlock();
                ThreadUtil.sleep(delay);
                int _delay = delay;
                int _retryTimes = retryTimes;
                _delay *= 2;
                if(_retryTimes-- == 0){
                    return;
                }
                sendMessage(message, _delay, _retryTimes);
            }
        });
    }

    public static void setOnMessageListener(MQMessageListener messageListener){
        vector.add(messageListener);
    }

    public interface MQMessageListener{
        void call(Message message);
    }

}
