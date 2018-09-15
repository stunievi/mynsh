package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.mobile.response.ReadMessageResponse;
import com.beeasy.hzback.modules.mobile.response.UnreadMessageResponse;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.common.entity.*;
import com.beeasy.common.entity.MessageRead;
import com.beeasy.common.entity.SystemFile;
import com.beeasy.common.entity.User;
import com.beeasy.hzback.modules.system.form.MessageAdd;
import com.beeasy.hzback.modules.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

@Slf4j
@Service
@Transactional
public class MessageService implements IMessageService {

    @Autowired
    EntityManager entityManager;
    @Autowired
    ISystemFileDao systemFileDao;
    @Autowired
    UserService userService;
    @Autowired
    IMessageDao messageDao;
    //    @Autowired
//    CloudDiskService cloudDiskService;
    //    @Autowired
//    IMessageSessionDao messageSessionDao;
    @Autowired
    IMessageReadDao messageReadDao;
    @Autowired
    IDownloadFileTokenDao downloadFileTokenDao;

    @Autowired
    IUserDao userDao;

    public List<ReadMessageResponse> userReadMessage(long fromUid, Long... toUids) {
        List<ReadMessageResponse> responses = new ArrayList<>();
        User user = userService.findUser(fromUid);
        if (null == user) {
            return responses;
        }
        List<MessageRead> reads = messageReadDao.getUser2UsersRead(user, toUids);
        return reads.stream().map(messageRead -> {
            entityManager.refresh(messageRead, PESSIMISTIC_WRITE);
            messageRead.setUnreadNum(0);
            entityManager.merge(messageRead);
            return new ReadMessageResponse(messageRead.getToType(), messageRead.getToId(), true);
        }).collect(Collectors.toList());
    }

    public Optional<Map> sendMessage(long fromUid, long toUid, final long fileId) {
        return sendMessage(fromUid, toUid, "").map(message -> {
            SystemFile systemFile = systemFileDao.findById(fileId).orElse(null);
            if(null == systemFile){
                return null;
            }
            message.setType(Message.Type.FILE);
            message.setContent(systemFile.getFileName());
            message.setLinkId(systemFile.getId());
            message = messageDao.save(message);

            return applyDownload(fromUid, message);
        });
    }

    public Optional<Message> sendMessage(long fromUid, long toUid, String content, String uuid) {
        User fromUser = userService.findUser(fromUid);
        if (null == fromUser) return Optional.empty();
        User toUser = userService.findUser(toUid);
        if (null == toUser) return Optional.empty();

        if (fromUid == toUid) return Optional.empty();

        Message message = new Message();
        message.setFromType(Message.LinkType.USER);
        message.setToType(Message.LinkType.USER);
        message.setFromId(fromUid);
        message.setToId(toUid);
        message.setContent(content);
        message.setUuid(uuid);
        message.setFromName(fromUser.getTrueName());
        message.setToName(toUser.getTrueName());
        message = messageDao.save(message);
        //发件人的已读设置为该条信息
        MessageRead messageRead = messageReadDao.getUser2UserRead(fromUser, toUid).orElse(null);
        if (null != messageRead) {
//            messageRead = entityManager.find(MessageRead.class,messageRead.getId());
            entityManager.refresh(messageRead, PESSIMISTIC_WRITE);
            messageRead.setUnreadNum(0);
            entityManager.merge(messageRead);
        } else {
            messageRead = new MessageRead();
            messageRead.setUser(fromUser);
            messageRead.setToId(toUid);
            messageRead.setUnreadNum(0);
            messageReadDao.save(messageRead);
        }
//        if(null == messageRead){
//            messageRead = new MessageRead();
//            messageRead.setUser(fromUser);
//            messageRead.setToId(toUid);
//        }
        //自己发的表示都看过了
//        messageRead.setUnreadNum(0);
//        messageReadDao.save(messageRead);

        messageRead = messageReadDao.getUser2UserRead(toUser, fromUid).orElse(null);
        if (null != messageRead) {
//            messageRead = entityManager.find(MessageRead.class,messageRead.getId());
            entityManager.refresh(messageRead, PESSIMISTIC_WRITE);
            messageRead.setUnreadNum(messageRead.getUnreadNum() + 1);
            entityManager.merge(messageRead);
        } else {
            messageRead = new MessageRead();
            messageRead.setUser(toUser);
            messageRead.setToId(fromUid);
            messageRead.setUnreadNum(1);
            messageReadDao.save(messageRead);
        }

        return Optional.of(message);
    }

    public Optional<Message> sendMessage(long fromUid, long toUid, String content) {
        return sendMessage(fromUid, toUid, content, "");
    }

    public JSONObject applyDownload(final long uid, Message message) {
        if (!(message.getFromType().equals(Message.LinkType.USER) && message.getToType().equals(Message.LinkType.USER) && (message.getFromId().equals(uid) || message.getToId().equals(uid)))) {
            return (JSONObject) JSON.toJSON(message);
        }
        //如果不是文件
        if (!message.getType().equals(Message.Type.FILE)) {
            return (JSONObject) JSON.toJSON(message);
        }
        SystemFile file = systemFileDao.findById(message.getLinkId()).orElse(null);
        if(null == file){
            return (JSONObject) JSON.toJSON(message);
        }
        if(null == file.getExprTime() || file.getExprTime().before(new Date())){
            file.setToken(UUID.randomUUID().toString());
            file.setExprTime(new Date(System.currentTimeMillis() + 3600000));
            systemFileDao.save(file);
        }
        //延后1个小时
        else if(file.getExprTime().after(new Date())){
            file.setExprTime(new Date(System.currentTimeMillis() + 3600000));
            systemFileDao.save(file);
        }
        JSONObject object = (JSONObject) JSON.toJSON(message);
        object.put("token", file.getToken());
        return object;
    }

    public List<Message> getUserRecentMessages(long fromUid, long toUid, Long messageId) {
        PageRequest pageRequest = new PageRequest(0, 20);
        if (messageId == null) {
            messageId = Long.MAX_VALUE;
        }
        return messageDao.findUser2UserRecentMessages(fromUid, toUid, messageId, pageRequest);
    }

    public List<UnreadMessageResponse> getUnreadNums(Long fromUid, Long... toUserIds) {
        User user = userService.findUser(fromUid);
        if (null == user) return new ArrayList<>();
        List<MessageRead> reads = user.getReadList();
        List<Long> toIds = Arrays.asList(toUserIds);
        PageRequest page = new PageRequest(0, 1);
        List<UnreadMessageResponse> list = (List<UnreadMessageResponse>) reads.stream()
                .filter(item -> {
                    //有未读信息的, 强制返回
                    if (item.getUnreadNum() > 0) return true;
                    //在查询列表里的, 强制返回
                    if (toIds.contains(item.getToId())) return true;
                    return false;
                })
                .map(item -> {
                    UnreadMessageResponse messageResponse = new UnreadMessageResponse();
                    messageResponse.setToId(item.getToId());
                    messageResponse.setUnreadNums(item.getUnreadNum());
                    //得到最新的未读消息
                    List<Message> messages = messageDao.findUser2UserRecentMessages(fromUid, item.getToId(), Long.MAX_VALUE, page);
                    if (messages.size() > 0) {
                        messageResponse.setLastMessage(messages.get(0));
                    }
                    return messageResponse;
                }).collect(Collectors.toList());

        return list;
    }

    public Page<Message> getMessages(long uid, Boolean read, Pageable pageable) {
        Specification query = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("fromUser"), uid));
                if (null != read) {
                    predicates.add(cb.equal(root.get("checked"), read));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return messageDao.findAll(query, pageable);
    }


//    private Optional<MessageSession> getSession(Long... uids) {
//        Set<User> users = new HashSet<>();
//        for (Long uid : uids) {
//            User user = userService.findUser(uid);
//            if (null == user) return Optional.empty();
//            users.add(user);
//        }
//        List<Long> uidList = Arrays.asList(uids);
//        Collections.sort(uidList);
//        List<String> strList = uidList.stream().map(i -> "(" + i + ")").collect(Collectors.toList());
//        String str = String.join("", strList);
//        MessageSession session = messageSessionDao.findFirstByUsersStr(str).orElse(null);
//        if (null == session) {
//            session = new MessageSession();
//            session.setUsers(users);
//            session.setUsersStr(str);
//            session = messageSessionDao.save(session);
//            //添加未读记录
//            for (User user : users) {
//                MessageRead read = new MessageRead();
//                read.setSession(session);
//                read.setUser(user);
//                read.setMessageId(0L);
//                messageReadDao.save(read);
//            }
//            return findSession(session.getId());
//        }
//        return Optional.ofNullable(session);
//    }
//
//    public Optional<MessageSession> findSession(long id){
//        return Optional.ofNullable(messageSessionDao.findOne(id));
//    }
}
