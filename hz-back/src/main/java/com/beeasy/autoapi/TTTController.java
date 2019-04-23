package com.beeasy.autoapi;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.Result;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.blob.BlobTransferPolicy;
import org.apache.activemq.blob.BlobUploader;
import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.osgl.util.IO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RequestMapping("/aaa")
@RestController
@Component
public class TTTController {

    @Autowired
    JmsMessagingTemplate jmsTemplate;

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic( ConnectionFactory activeMQConnectionFactory){
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(activeMQConnectionFactory);
return bean;
    }
    @JmsListener(destination = "my_msg", containerFactory = "jmsListenerContainerTopic")
    public void test(Object o) throws IOException, JMSException {
        if(o instanceof TextMessage){
            System.out.println(((TextMessage) o).getText());
        } else if(o instanceof BlobMessage){
            String file = IO.readContentAsString(((BlobMessage) o).getInputStream());
            try(
                InputStream is = ((BlobMessage) o).getInputStream()
                ){
                byte[] bs = new byte[1024];
                int len = -1;
                while((len = is.read(bs)) > 0){
                    System.out.println(new String(bs, 0, len, StandardCharsets.UTF_8));
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
            System.out.println(file);
        }
    }


    @RequestMapping("/fff")
    public void testt(){
        ActiveMQTopic topic = new ActiveMQTopic("my_msg");
        jmsTemplate.convertAndSend(topic, new File("D:\\work\\cu\\History\\GetHistorytShareHolder.json"));

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


    }


    @RequestMapping("/ff")
    public Result ttt(
            @RequestBody JSONObject object
            ){
        return Result.ok();
    }
}
