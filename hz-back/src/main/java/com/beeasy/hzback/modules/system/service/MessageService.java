package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.setting.entity.User;
import com.beeasy.hzback.modules.system.dao.IMessageDao;
import com.beeasy.hzback.modules.system.entity.Message;
import com.beeasy.hzback.modules.system.entity.MessageFile;
import com.beeasy.hzback.modules.system.form.MessageAdd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MessageService implements IMessageService {
    @Autowired
    UserService userService;
    @Autowired
    IMessageDao messageDao;
    @Autowired
    CloudDiskService cloudDiskService;


    private void sendMessage(User fromUser, User toUser, String title, String content,Set<MessageAdd.File> files){
        Message message = new Message();
        message.setFromUser(fromUser);
        message.setToUser(toUser);
        message.setTitle(title);
        message.setContent(content);
        //保存附件
        if(null != files){
            files.forEach(file -> {
                //个人共享
                if(file.getType().equals(ICloudDiskService.DirType.USER)){
                    cloudDiskService.shareTo(fromUser.getId(),toUser.getId(), ICloudDiskService.DirType.USER,file.getFileId()).ifPresent(fileIndex -> {
                        MessageFile messageFile = new MessageFile();
                        messageFile.setType(ICloudDiskService.DirType.USER);
                        messageFile.setLinkId(fileIndex.getId());
                        messageFile.setMessage(message);
                        message.getFiles().add(messageFile);
                    });
                }
                else if(file.getType().equals(ICloudDiskService.DirType.COMMON)){
                    cloudDiskService.findFile(0, ICloudDiskService.DirType.COMMON,file.getFileId()).ifPresent(fileIndex -> {
                        MessageFile messageFile = new MessageFile();
                        messageFile.setType(ICloudDiskService.DirType.COMMON);
                        messageFile.setLinkId(fileIndex.getId());
                        messageFile.setMessage(message);
                        message.getFiles().add(messageFile);
                    });
                }
                else{
                    return;
                }
            });
        }
        messageDao.save(message);
    }


    public void sendMessage(long fromUid, Set<Long> toUserIds, String title, String content, Set<MessageAdd.File> files){
        userService.findUser(fromUid)
                .ifPresent(user -> {
                    for (long toUserId : toUserIds) {
                        userService.findUser(toUserId)
                                .ifPresent(toUser -> {
                                    sendMessage(user,toUser,title,content,files);
                                });

                    }
                });
    }

    public void readMessage(long fromUid, Set<Long> messageIds){
        if(messageIds.size() == 0){
            return;
        }
        userService.findUser(fromUid)
                .ifPresent(user -> {
                    messageDao.readMessagesByFromUserAndIdIn(user,messageIds);
                });
    }


    public Page<Message> getMessages(long uid, Boolean read, Pageable pageable){
        Specification query = new Specification(){
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("fromUser"),uid));
                if(null != read){
                    predicates.add(cb.equal(root.get("checked"),read));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return messageDao.findAll(query,pageable);
    }


}
