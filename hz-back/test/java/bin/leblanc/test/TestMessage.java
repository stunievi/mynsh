package bin.leblanc.test;

import com.beeasy.hzback.modules.message.dao.IMessageContentDao;
import com.beeasy.hzback.modules.message.entity.Message;
import com.beeasy.hzback.modules.message.entity.MessageContent;
import com.beeasy.hzback.modules.message.dao.IMessageDao;
import com.beeasy.hzback.Application;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import com.beeasy.hzback.modules.setting.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMessage {

    @Autowired
    IUserDao userDao;

    @Autowired
    IMessageDao messageDao;

    @Autowired
    IMessageContentDao messageContentDao;

    @Test
    public void test1(){
        /**
         * 插入一条新信息
         */
        log.info("f");
        User user = userDao.findOne(9);
        Message message = new Message();
        message.setFromUser(user);
        message.setToUser(user.getId());
        messageDao.save(message);


        MessageContent messageContent = new MessageContent();
        messageContent.setMessage(message);
        messageContent.setContent("fuck u");
        messageContentDao.save(messageContent);

    }
}
