package com.beeasy.loadqcc.config;

import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.io.File;
import java.io.IOException;

@Configuration
public class MQConfig {

    @Autowired
    private JmsMessagingTemplate jmsTemplate;

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory activeMQConnectionFactory){
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);

        jmsTemplate.setJmsMessageConverter(new MessageConverter() {
            @Override
            public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException {
                if(o instanceof GenericMessage){
                    if(((GenericMessage) o).getPayload() instanceof File){
                        return ((ActiveMQSession)session).createBlobMessage((File) ((GenericMessage) o).getPayload());
                    } else if (((GenericMessage) o).getPayload() instanceof String){
                        return session.createTextMessage((String) ((GenericMessage) o).getPayload());
                    }
                }
                return null;
            }

            @Override
            public Object fromMessage(Message message) throws JMSException, MessageConversionException {
                if(message instanceof BlobMessage) {
                    try {
                        return ((BlobMessage) message).getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });

        return bean;
    }
}
//
//
//配置：
//
//        发送：
//        jmsTemplate.convertAndSend(topic, new File("D:\\work\\cu\\History\\GetHistorytShareHolder.json"));
//        接收：
//@JmsListener(destination = "my_msg", containerFactory = "jmsListenerContainerTopic")
//public void test(Object o) throws IOException, JMSException {
//        if(o instanceof TextMessage){
//        System.out.println(((TextMessage) o).getText());
//        } else if(o instanceof BlobMessage){
//        String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
//        System.out.println(file);
//        }
//        }
