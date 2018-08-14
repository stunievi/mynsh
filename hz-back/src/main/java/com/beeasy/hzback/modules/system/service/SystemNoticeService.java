package com.beeasy.hzback.modules.system.service;

import com.beeasy.hzback.modules.system.dao.ISystemNoticeDao;
import com.beeasy.common.entity.SystemNotice;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class SystemNoticeService {
    @Autowired
    ISystemNoticeDao noticeDao;

    /**
     * 消息列表查询
     *
     * @param uid
     * @param request
     * @param pageable
     * @return
     */
    public Page<SystemNotice> getMessageList(long uid, SearchRequest request, Pageable pageable) {
        Specification query = new Specification() {
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                //限定uid
                predicates.add(cb.equal(root.get("userId"), uid));
                if (null != request.getState()) {
                    predicates.add(cb.equal(root.get("state"), request.getState()));
                }
                if (null != request.getType()) {
                    predicates.add(cb.equal(root.get("type"), request.getType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return noticeDao.findAll(query, pageable);
    }

    /**
     * 更新已读
     *
     * @param uid
     * @param nids
     * @return
     */
    public boolean readNotice(long uid, Collection<Long> nids) {
        return noticeDao.updateRead(uid, nids) > 0;
    }


    @Async
    public void addNotice(SystemNotice.Type type, Collection<Long> toUids, String content, Object data) {
        for (Long toUid : toUids) {
            SystemNotice notice = new SystemNotice();
            notice.setState(SystemNotice.State.UNREAD);
            notice.setType(type);
            notice.setUserId(toUid);
            notice.setContent(content);
            notice.setBindData(data);
            noticeDao.save(notice);
        }
    }

    @Async
    public void addNotice(SystemNotice.Type type, long toUid, String content, Object data) {
        SystemNotice notice = new SystemNotice();
        notice.setState(SystemNotice.State.UNREAD);
        notice.setType(type);
        notice.setUserId(toUid);
        notice.setContent(content);
        notice.setBindData(data);
        noticeDao.save(notice);
    }


    @Data
    public static class SearchRequest {
        SystemNotice.State state;
        SystemNotice.Type type;
    }
}
