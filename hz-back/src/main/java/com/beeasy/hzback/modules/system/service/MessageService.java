package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.mobile.response.ReadMessageResponse;
import com.beeasy.hzback.modules.mobile.response.UnreadMessageResponse;
import com.beeasy.hzback.modules.system.dao.*;
import com.beeasy.hzback.modules.system.entity.*;
import com.beeasy.hzback.modules.system.form.MessageAdd;
import lombok.extern.slf4j.Slf4j;
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
    @Autowired
    CloudDiskService cloudDiskService;
    //    @Autowired
//    IMessageSessionDao messageSessionDao;
    @Autowired
    IMessageReadDao messageReadDao;
    @Autowired
    IDownloadFileTokenDao downloadFileTokenDao;

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
//            files.forEach(bytes -> {
//                //个人共享
//                if (bytes.getType().equals(ICloudDiskService.DirType.USER)) {
//                    cloudDiskService.shareTo(fromUser.getId(), toUser.getId(), ICloudDiskService.DirType.USER, bytes.getFileId()).ifPresent(fileIndex -> {
//                        MessageFile messageFile = new MessageFile();
//                        messageFile.setType(ICloudDiskService.DirType.USER);
//                        messageFile.setLinkId(fileIndex.getId());
////                        messageFile.setMessage(message);
////                        message.getFiles().add(messageFile);
//                    });
//                } else if (bytes.getType().equals(ICloudDiskService.DirType.COMMON)) {
//                    cloudDiskService.findFile(0, ICloudDiskService.DirType.COMMON, bytes.getFileId()).ifPresent(fileIndex -> {
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


    /**
     * PC上传文件
     *
     * @param uid
     * @param file
     * @return
     */
    public Optional<Long> pcUploadFile(long uid, MultipartFile file) {
        try {
            SystemFile systemFile = new SystemFile();
            systemFile.setBytes(file.getBytes());
            systemFile.setType(SystemFile.Type.MESSAGE);
            systemFile.setFileName(file.getOriginalFilename());
            systemFile.setBytes(file.getBytes());
            systemFile = systemFileDao.save(systemFile);
            return Optional.ofNullable(systemFile.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * 删除临时文件
     *
     * @param uid
     * @param fileId
     * @return
     */
    public boolean pcRemoveFile(long uid, long fileId) {
        SystemFile systemFile = systemFileDao.findFirstByIdAndType(fileId, SystemFile.Type.MESSAGE).orElse(null);
        if (null == systemFile) {
            return false;
        }
        systemFileDao.delete(systemFile);
        return true;
    }

    /**
     * pc发送消息
     *
     * @param fromUid
     * @param toUid
     * @param content
     * @param fileIds
     * @return
     */
    public Result pcSendMessage(long fromUid, long toUid, String content, List<Long> fileIds) {
        List<Message> result = new ArrayList<>();
        Message mainMessage = sendMessage(fromUid, toUid, content).orElse(null);
        if (null == mainMessage) {
            return Result.error("发送失败");
        }
        String uuid = UUID.randomUUID().toString();
        mainMessage.setCommonUUID(uuid);
        messageDao.save(mainMessage);
        result.add(mainMessage);
        for (Long fileId : fileIds) {
            int count = systemFileDao.countByIdAndType(fileId, SystemFile.Type.MESSAGE);
            if (count == 0) {
                continue;
            }
            Message message = sendMessage(fromUid, toUid, "").orElse(null);
            if (null == message) {
                continue;
            }
            message.setCommonUUID(uuid);
            message.setType(Message.Type.FILE);
            message.setLinkId(fileId);
            messageDao.save(message);
            result.add(message);
        }
        return Result.ok(result);
    }

    public List<ReadMessageResponse> userReadMessage(long fromUid, Long... toUids) {
        List<ReadMessageResponse> responses = new ArrayList<>();
        User user = userService.findUser(fromUid).orElse(null);
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

    public Optional<Message> sendMessage(long fromUid, long toUid, MultipartFile file) {
        return sendMessage(fromUid, toUid, "").map(message -> {
            //保存文件
            SystemFile systemFile = new SystemFile();
            try {
                systemFile.setBytes(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            systemFile.setType(SystemFile.Type.MESSAGE);
            systemFile.setFileName(file.getOriginalFilename());
            systemFile = systemFileDao.save(systemFile);
            message.setType(Message.Type.FILE);
            message.setContent(file.getOriginalFilename());
            message.setLinkId(systemFile.getId());
            return messageDao.save(message);
        });
    }

    public Optional<Message> sendMessage(long fromUid, long toUid, String content, String uuid) {
        User fromUser = userService.findUser(fromUid).orElse(null);
        if (null == fromUser) return Optional.empty();
        User toUser = userService.findUser(toUid).orElse(null);
        if (null == toUser) return Optional.empty();

        if (fromUid == toUid) return Optional.empty();

        Message message = new Message();
        message.setFromType(Message.LinkType.USER);
        message.setToType(Message.LinkType.USER);
        message.setFromId(fromUid);
        message.setToId(toUid);
        message.setContent(content);
        message.setUuid(uuid);
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

    public String applyDownload(final long uid, final long id){
        Message message = messageDao.findById(id).orElse(null);
        if(null == message){
            return "";
        }
        if(!(message.getFromType().equals(Message.LinkType.USER) && message.getToType().equals(Message.LinkType.USER) && (message.getFromId().equals(uid) || message.getToId().equals(uid)) )){
            return "";
        }
        //如果不是文件
        if(null == message.getLinkId()){
            return "";
        }
        DownloadFileToken token = new DownloadFileToken();
        token.setExprTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000));
        token.setToken(UUID.randomUUID().toString());
        token.setFileId(message.getLinkId());
        downloadFileTokenDao.save(token);
        return token.getToken();
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

//    public List<MessageSession> getUnreadMessageList(long uid) {
//        List<MessageSession> ret = new ArrayList<>();
//        User user = userService.findUser(uid).orElse(null);
//        if (null == user) return ret;
//        user.getReadList().stream().forEach(messageRead -> {
//            List<Message> list = messageDao.findAllBySessionAndIdGreaterThanOrderBySendTimeDesc(messageRead.getSession(), messageRead.getMessageId());
//            messageRead.getSession().setMessageList(list);
//            ret.add(messageRead.getSession());
//        });
//        return ret;
//    }

//    public List<UnreadNum> getUserEachSessionUnreadNums(long uid){
//        List<Object[]> list = messageDao.getUserEachSessionUnreadNums(uid);
//        return list.stream().map(obj -> {
//            return new UnreadNum(NumberUtils.toLong(String.valueOf(obj[0])),NumberUtils.toLong(String.valueOf(obj[1])));
//        }).collect(Collectors.toList());
//    }

    public List<Message> getUserRecentMessages(long fromUid, long toUid, Long messageId) {
        PageRequest pageRequest = new PageRequest(0, 20);
        if (messageId == null) {
            messageId = Long.MAX_VALUE;
        }
        return messageDao.findUser2UserRecentMessages(fromUid, toUid, messageId, pageRequest);
    }

    public List<UnreadMessageResponse> getUnreadNums(Long fromUid, Long... toUserIds) {
        User user = userService.findUser(fromUid).orElse(null);
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
//            User user = userService.findUser(uid).orElse(null);
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
