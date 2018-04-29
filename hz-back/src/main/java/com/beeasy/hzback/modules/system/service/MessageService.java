package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.MessageAdd;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    CloudDiskService cloudDiskService;
    @Autowired
    IMessageSessionDao messageSessionDao;
    @Autowired
    IMessageReadDao messageReadDao;

    @Autowired
    IUserDao userDao;

    private void sendMessage(User fromUser, User toUser, String title, String content, Set<MessageAdd.File> files) {
//        Message message = new Message();
////        message.setFromUser(fromUser);
////        message.setToUser(toUser);
////        message.setTitle(title);
//        message.setContent(content);
//        //保存附件
//        if (null != files) {
//            files.forEach(file -> {
//                //个人共享
//                if (file.getType().equals(ICloudDiskService.DirType.USER)) {
//                    cloudDiskService.shareTo(fromUser.getId(), toUser.getId(), ICloudDiskService.DirType.USER, file.getFileId()).ifPresent(fileIndex -> {
//                        MessageFile messageFile = new MessageFile();
//                        messageFile.setType(ICloudDiskService.DirType.USER);
//                        messageFile.setLinkId(fileIndex.getId());
////                        messageFile.setMessage(message);
////                        message.getFiles().add(messageFile);
//                    });
//                } else if (file.getType().equals(ICloudDiskService.DirType.COMMON)) {
//                    cloudDiskService.findFile(0, ICloudDiskService.DirType.COMMON, file.getFileId()).ifPresent(fileIndex -> {
//                        MessageFile messageFile = new MessageFile();
//                        messageFile.setType(ICloudDiskService.DirType.COMMON);
//                        messageFile.setLinkId(fileIndex.getId());
////                        messageFile.setMessage(message);
////                        message.getFiles().add(messageFile);
//                    });
//                } else {
//                    return;
//                }
//            });
//        }
//        messageDao.save(message);
        return;
    }

    //
    public void sendMessage(long fromUid, Set<Long> toUserIds, String title, String content, Set<MessageAdd.File> files) {
//        userService.findUser(fromUid)
//                .ifPresent(user -> {
//                    for (long toUserId : toUserIds) {
//                        userService.findUser(toUserId)
//                                .ifPresent(toUser -> {
//                                    sendMessage(user, toUser, title, content, files);
//                                });
//
//                    }
//                });
        return;
    }


    public Optional<Message> sendMessage(Long fromUid, Long toUid, byte[] bytes) {
        return getSession(fromUid, toUid).map(messageSession -> {
            Message message = new Message();
            message.setContent("");
            SystemFile file = new SystemFile();
            file.setType(SystemFile.FileType.MESSAGE);
            file.setFile(bytes);
            file = systemFileDao.save(file);
            if (null == file.getId()) {
                return null;
            }
            message.getFiles().add(file);
            return messageDao.save(message);
        });
    }

    public Optional<Message> sendMessage(long fromUid, long toUid, String content) {
        if(fromUid == toUid) return Optional.empty();
        MessageSession session = getSession(fromUid, toUid).orElse(null);
        if (null == session) return Optional.empty();

        Message message = new Message();
        message.setSession(session);
        message.setContent(content);
        message.setFromUserId(fromUid);
        message = messageDao.save(message);
        //发件人的已读设置为该条信息
        MessageRead messageRead = messageReadDao.findFirstBySessionAndUser_Id(session,fromUid).orElse(null);
        if(null != messageRead){
            messageRead.setMessageId(message.getId());
            messageReadDao.save(messageRead);
        }
        return Optional.of(message);
    }

//    public void readMessage(long fromUid, Set<Long> messageIds){
//        if(messageIds.size() == 0){
//            return;
//        }
//        userService.findUser(fromUid)
//                .ifPresent(user -> {
//                    messageDao.readMessagesByFromUserAndIdIn(user,messageIds);
//                });
//    }

    public List<MessageSession> getUnreadMessageList(long uid) {
        List<MessageSession> ret = new ArrayList<>();
        User user = userService.findUser(uid).orElse(null);
        if (null == user) return ret;
        user.getReadList().stream().forEach(messageRead -> {
            List<Message> list = messageDao.findAllBySessionAndIdGreaterThanOrderBySendTimeDesc(messageRead.getSession(), messageRead.getMessageId());
            messageRead.getSession().setMessageList(list);
            ret.add(messageRead.getSession());
        });
        return ret;
    }

    public List<UnreadNum> getUserEachSessionUnreadNums(long uid){
        List<Object[]> list = messageDao.getUserEachSessionUnreadNums(uid);
        return list.stream().map(obj -> {
            return new UnreadNum(NumberUtils.toLong(String.valueOf(obj[0])),NumberUtils.toLong(String.valueOf(obj[1])));
        }).collect(Collectors.toList());
    }

    public List<Message> getUserRecentMessages(long fromUid, long toUid, Long messageId){
        List<Message> ret = new ArrayList<>();
        MessageSession session = getSession(fromUid,toUid).orElse(null);
        if(null == session) return ret;
        PageRequest pageRequest = new PageRequest(0,20);
        if(messageId == null){
             return messageDao.getAllBySessionOrderBySendTimeDesc(session,pageRequest);
        }
        else{
            return messageDao.getAllBySessionAndIdLessThanOrderBySendTimeDesc(session,messageId,pageRequest);
        }
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


    private Optional<MessageSession> getSession(Long... uids) {
        Set<User> users = new HashSet<>();
        for (Long uid : uids) {
            User user = userService.findUser(uid).orElse(null);
            if (null == user) return Optional.empty();
            users.add(user);
        }
        List<Long> uidList = Arrays.asList(uids);
        Collections.sort(uidList);
        List<String> strList = uidList.stream().map(i -> "(" + i + ")").collect(Collectors.toList());
        String str = String.join("", strList);
        MessageSession session = messageSessionDao.findFirstByUsersStr(str).orElse(null);
        if (null == session) {
            session = new MessageSession();
            session.setUsers(users);
            session.setUsersStr(str);
            session = messageSessionDao.save(session);
            //添加未读记录
            for (User user : users) {
                MessageRead read = new MessageRead();
                read.setSession(session);
                read.setUser(user);
                read.setMessageId(0L);
                messageReadDao.save(read);
            }
            return findSession(session.getId());
        }
        return Optional.ofNullable(session);
    }

    public Optional<MessageSession> findSession(long id){
        return Optional.ofNullable(messageSessionDao.findOne(id));
    }
}
