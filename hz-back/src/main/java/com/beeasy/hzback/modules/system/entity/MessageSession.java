package com.beeasy.hzback.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "t_message_session")
public class MessageSession {
   @Id
   @GeneratedValue
   Long id;

   @JSONField(serialize = false)
   @ManyToMany
   @JoinTable(name = "T_MESSAGE_SESSION_USER",
           joinColumns = {
                   @JoinColumn(name = "MESSAGE_SESSION_ID", referencedColumnName = "ID")
           },
           inverseJoinColumns = {
                   @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
           }
   )
   Set<User> users = new HashSet<>();

   String usersStr;


   @Transient
   List<Message> messageList;
}
