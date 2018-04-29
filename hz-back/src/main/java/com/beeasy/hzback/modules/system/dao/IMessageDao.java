package com.beeasy.hzback.modules.system.dao;

import com.beeasy.hzback.modules.system.entity.Message;
import com.beeasy.hzback.modules.system.entity.MessageSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IMessageDao extends JpaRepository<Message,Long>, JpaSpecificationExecutor  {
//    void readMessagesByFromUserAndIdIn(User user, Set<Long> ids);

List<Message> findAllBySessionAndIdGreaterThanOrderBySendTimeDesc(MessageSession session, Long id);

    @Query(value = "SELECT m.* FROM t_message m WHERE( SELECT count(*) FROM t_message m2 WHERE m2.session_id = m.session_id AND m.send_time < m2.send_time) < :num AND m.session_id IN( SELECT mr.session_id FROM t_message_read mr WHERE mr.user_id = :uid)",nativeQuery = true)
    List<Message> getUserEachSessionRecentMessages(long uid,int num);

    @Query(value = "SELECT count(*) AS num , m.session_id AS sessionId FROM t_message m WHERE( SELECT count(*) FROM t_message m2 WHERE m2.session_id = m.session_id AND m.send_time < m2.send_time) >= 0 AND m.session_id IN( SELECT mr.session_id FROM t_message_read mr WHERE mr.user_id = :uid AND m.id > mr.message_id) GROUP BY session_id",nativeQuery = true)
    List getUserEachSessionUnreadNums(long uid);

    List<Message> getAllBySessionAndIdLessThanOrderBySendTimeDesc(MessageSession session, Long id, Pageable pageable);
    List<Message> getAllBySessionOrderBySendTimeDesc(MessageSession session, Pageable pageable);





}
