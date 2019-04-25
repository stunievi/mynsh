package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;

import javax.jms.*;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static com.beeasy.zed.DBService.config;

public class MQService {

    private static ActiveMQConnection connection = null;
    private static AtomicBoolean ready = new AtomicBoolean(false);
    private static Vector<Object[]> listeners = new Vector<>();

    public static void await() {
        //如果根本没初始化，那么不再等待
        while(!ready.get()){
             ThreadUtil.sleep(100);
        }
    }


    public static void init(){
        ThreadUtil.execAsync(() -> {
            try{
                //1、创建工厂连接对象，需要制定ip和端口号
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(config.getString("mqserver"));
                //2、使用连接工厂创建一个连接对象
                connection = (ActiveMQConnection) connectionFactory.createConnection();
                //3、开启连接
                //4、使用连接对象创建会话（session）对象

                connection.setExceptionListener(new ExceptionListener() {
                    @Override
                    public void onException(JMSException e) {
                        ready.set(false);
                        ThreadUtil.sleep(300);
                        init();
                    }
                });

                connection.start();
                ready.set(true);

                for (Object[] listener : listeners) {
                    bindListener((String)listener[0], (String)listener[1], (MQMessageListener) listener[2]);
                }
            }
            catch (Exception ee){
                ready.set(false);
                ee.printStackTrace();
                ThreadUtil.sleep(300);
                init();
            }
        });
    }

    public static void sendMessage(String type, String topic, Object message){
        ThreadUtil.execAsync(() -> {
            ActiveMQSession session = null;
            MessageProducer producer = null;
            await();
            try {
                session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                if(type.equals("topic")){
                    Topic t = session.createTopic(topic);
                    producer = session.createProducer(t);

                } else if(type.equals("queue")){
                    Queue t = session.createQueue(topic);
                    producer = session.createProducer(t);
                }
                if(message instanceof String){
                    TextMessage msg = session.createTextMessage((String) message);
                    producer.send(msg);
                } else if(message instanceof File){
                    BlobMessage blobMessage = (BlobMessage) session.createBlobMessage((File) message);
                    producer.send(blobMessage);
                } else if(message instanceof FileRequest){
                    BlobMessage blobMessage = (BlobMessage) session.createBlobMessage((File) ((FileRequest) message).file);
                    blobMessage.setStringProperty("requestId", ((FileRequest) message).requestId);
                    blobMessage.setStringProperty("sourceRequest", ((FileRequest) message).sourceRequest);
                    producer.send(blobMessage);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            } finally {
                if (producer != null) {
                    try {
                        producer.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
                if (session != null) {
                    try {
                        session.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private static void bindListener(String type, String topic, MQMessageListener listener){
        ActiveMQSession session = null;
        MessageConsumer consumer = null;
        try {
            session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            if (type.equals("topic")) {
                Topic t = session.createTopic(topic);
                consumer = session.createConsumer(t);
            } else {
                Queue q = session.createQueue(topic);
                consumer = session.createConsumer(q);
            }
            consumer.setMessageListener(m -> {
                try {
                    listener.call(m);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public static void listenMessage(String type, String topic, MQMessageListener listener){
//        //暂时也这么调用一份
//        if (type.equals("topic")) {
//            listenMessage("queue", topic, listener);
//        }
        listeners.add(new Object[]{type, topic, listener});
        if(!ready.get()){
            return;
        }
        ThreadUtil.execAsync(() -> bindListener(type, topic, listener));
    }


    public interface MQMessageListener{
        void call(Message message) throws JMSException;
    }

    public static class FileRequest{
        public String requestId;
        public String sourceRequest;
        public File file;

        public FileRequest(String requestId, String sourceRequest, File file) {
            this.requestId = requestId;
            this.sourceRequest = sourceRequest;
            this.file = file;
        }
    }


}
