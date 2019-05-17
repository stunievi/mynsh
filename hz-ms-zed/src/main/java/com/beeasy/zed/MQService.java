package com.beeasy.zed;

import cn.hutool.core.thread.ThreadUtil;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;

import javax.jms.*;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.beeasy.zed.Config.config;


public class MQService extends AbstractService {

    private ActiveMQConnection connection = null;
    private volatile boolean ready = false;
    private Vector<Object[]> listeners = new Vector<>();
    private Vector<Object[]> messageBuffer = new Vector<>();
    private static MQService instance;

    static {
        instance = new MQService();
    }

    private MQService() {
    }

    public static MQService getInstance() {
        return instance;
    }

    @Override
    public void initAsync() {
        inited = ThreadUtil.execAsync(this::init);
    }

    @Override
    public void initSync() {
        //该服务只能异步初始化
        initAsync();
    }


    private synchronized void init() {
        try {
            //1、创建工厂连接对象，需要制定ip和端口号
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(config.mqserver);
            //2、使用连接工厂创建一个连接对象
            connection = (ActiveMQConnection) connectionFactory.createConnection();
            //3、开启连接
            //4、使用连接对象创建会话（session）对象

            connection.setExceptionListener(new ExceptionListener() {
                @Override
                public synchronized void onException(JMSException e) {
                    ready = false;
                    ThreadUtil.sleep(300);
                    init();
                }
            });

            connection.start();
            ready = true;

            for (Object[] objects : messageBuffer) {
                sendMessage((String) objects[0], (String) objects[1], (String) objects[2]);
            }
            messageBuffer.clear();

            for (Object[] listener : listeners) {
                bindListener((String) listener[0], (String) listener[1], (MQMessageListener) listener[2]);
            }

            System.out.println("mqservice boot success");

        } catch (Exception ee) {
            ready = false;
            ee.printStackTrace();
            ThreadUtil.sleep(300);
            init();
        }
    }


    public synchronized void sendMessage(String type, String topic, Object message) {
        if (!ready) {
            messageBuffer.add(new Object[]{type, topic, message});
            return;
        }

        ActiveMQSession session = null;
        MessageProducer producer = null;
        try {
            session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            if (type.equals("topic")) {
                Topic t = session.createTopic(topic);
                producer = session.createProducer(t);

            } else if (type.equals("queue")) {
                Queue t = session.createQueue(topic);
                producer = session.createProducer(t);
            }
            if (message instanceof String) {
                TextMessage msg = session.createTextMessage((String) message);
                producer.send(msg);
            } else if (message instanceof File) {
                BlobMessage blobMessage = (BlobMessage) session.createBlobMessage((File) message);
                producer.send(blobMessage);
            } else if (message instanceof FileRequest) {
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
    }

    private synchronized void bindListener(String type, String topic, MQMessageListener listener) {
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


    public synchronized void listenMessage(String type, String topic, MQMessageListener listener) {
//        //暂时也这么调用一份
//        if (type.equals("topic")) {
//            listenMessage("queue", topic, listener);
//        }
        listeners.add(new Object[]{type, topic, listener});
        if (!ready) {
            return;
        }
        bindListener(type, topic, listener);
    }


    public interface MQMessageListener {
        void call(Message message) throws JMSException;
    }

    public static class FileRequest {
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
