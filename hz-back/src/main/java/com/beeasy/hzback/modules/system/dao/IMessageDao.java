package com.beeasy.hzback.modules.system.dao;

import com.beeasy.common.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IMessageDao extends JpaRepository<Message, Long>, JpaSpecificationExecutor {
//    void readMessagesByFromUserAndIdIn(User user, Set<Long> ids);

//List<Message> findAllBySessionAndIdGreaterThanOrderBySendTimeDesc(MessageSession session, Long id);
//
//    @Query(value = "SELECT m.* FROM t_message m WHERE( SELECT count(*) FROM t_message m2 WHERE m2.session_id = m.session_id AND m.send_time < m2.send_time) < :num AND m.session_id IN( SELECT mr.session_id FROM t_message_read mr WHERE mr.user_id = :uid)",nativeQuery = true)
//    List<Message> getUserEachSessionRecentMessages(long uid,int num);
//
//    @Query(value = "SELECT count(*) AS num , m.session_id AS sessionId FROM t_message m WHERE( SELECT count(*) FROM t_message m2 WHERE m2.session_id = m.session_id AND m.send_time < m2.send_time) >= 0 AND m.session_id IN( SELECT mr.session_id FROM t_message_read mr WHERE mr.user_id = :uid AND m.id > mr.message_id) GROUP BY session_id",nativeQuery = true)
//    List getUserEachSessionUnreadNums(long uid);
//
//    List<Message> getAllBySessionAndIdLessThanOrderBySendTimeDesc(MessageSession session, Long id, Pageable pageable);
//    List<Message> getAllBySessionOrderBySendTimeDesc(MessageSession session, Pageable pageable);


    @Query(
            value = "select m from Message m where (m.fromType = 0 and m.toType = 0) and ((m.fromId = :fromId and m.toId = :toId) or (m.fromId = :toId and m.toId = :fromId)) and m.id < :messageId order by m.sendTime desc ")
    List<Message> findUser2UserRecentMessages(@Param("fromId") long fromId, @Param("toId") long toId, @Param("messageId") long messageId, Pageable pageable);


    @Query(value = "select m from Message m where (m.fromType = 0 and m.toType = 0) and ((m.fromId = :uid ) or (m.toId = :uid)) and m.type = 1 and m.linkId = :fileId")
    Optional<Message> findContainsFileMessage(@Param("uid") long uid, @Param("fileId") long fileId);

//    @Query(value = "SELECT count(*) , m.to_id FROM t_message m WHERE( SELECT count(*) FROM t_message m2 WHERE m.id < m2.id) >= 0 AND m.from_type = 0 AND m.from_id = :uid AND m.to_id IN( SELECT mr.to_id FROM t_message_read mr WHERE mr.message_id < m.id AND mr.to_type = m.to_type) GROUP BY m.to_id",nativeQuery = true)
//    List getUnreadNums(long uid);

}
