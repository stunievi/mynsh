//package com.beeasy.hzback.modules.system.dao;
//
//import com.beeasy.hzback.modules.system.entity.MessageSession;
//import com.beeasy.hzback.modules.system.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface IMessageSessionDao extends JpaRepository<MessageSession,Long>{
//
//    List<MessageSession> findAllByUsersContains(User user);
//    Optional<MessageSession> findFirstByUsersStr(String userStr);
//}
