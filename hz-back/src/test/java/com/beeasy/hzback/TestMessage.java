package com.beeasy.hzback;

import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.dao.IUserDao;
import com.beeasy.hzback.modules.system.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMessage {
    @Autowired
    MessageService messageService;
    @Autowired
    Utils utils;
    @Autowired
    IUserDao userDao;

    @Autowired
//    IMessageSessionDao messageSessionDao;
    @Test
    public void test() {
        Object o = messageService.getUnreadNums(609L, 559L, 560L);
        int b = 1;

//        User fromUser = utils.createFaker();
//        assertNotNull(fromUser);
//        User toUser = utils.createFaker();
//        assertNotNull(toUser);
//        Message message = messageService.sendMessage(fromUser.getId(),toUser.getId(),"fuck").orElse(null);
//        assertNotNull(message);
//
//        List<MessageSession> messages = messageService.getUnreadMessageList(fromUser.getId());
//        assertTrue(messages.size() > 0);
//        assertTrue(messages.get(0).getMessageList().size() == 0);
//
//        messages = messageService.getUnreadMessageList(toUser.getId());
//        assertTrue(messages.size() > 0);
//        assertTrue(messages.get(0).getMessageList().size() > 0);
//
//        userDao.delete(fromUser);
//        userDao.delete(toUser);
    }

    @Autowired
    IMessageDao messageDao;

    @Test
    public void test_recent() {
//        Object obj = messageService.getUserEachSessionUnreadNums(546);
//        int b = 1;
//
//        obj = messageDao.getUserEachSessionRecentMessages(546,20);

//        messageDao.findUser2UserRecentMessages(1,2,null);

    }
}
