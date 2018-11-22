package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.beeasy.hzback.entity.SysNotice;
import org.beetl.sql.core.SQLManager;
import org.osgl.util.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class NoticeService2 {
    @Autowired
    SQLManager sqlManager;

    @Async
    public void addNotice(SysNotice.Type type, Collection<Long> toUids, String content, Object data) {
        List<SysNotice> notices = C.newList();
        for (Long toUid : toUids) {
            notices.add(makeNotice(type,toUid,content,data));
        }
        sqlManager.insertBatch(SysNotice.class,notices);
    }

    @Async
    public void addNotice(SysNotice.Type type, long toUid, String content, Object data) {
        SysNotice notice = makeNotice(type,toUid,content,data);
        sqlManager.insert(notice);
    }


    public SysNotice makeNotice(SysNotice.Type type, long toUid, String content, Object data){
        SysNotice notice = new SysNotice();
        notice.setState(SysNotice.State.UNREAD);
        notice.setType(type);
        notice.setUserId(toUid);
        notice.setContent(content);
        notice.setBindData(JSON.toJSONString(data));
        notice.setAddTime(new Date());
        return notice;
    }

    public List<SysNotice> makeNotice(SysNotice.Type type, Collection<Long> toUids, String content, Object data){
        return toUids.stream().map(uid -> {
            return makeNotice(type,uid,content,data);
        }).collect(Collectors.toList());
    }

    @Async
    public void saveNotices(List<SysNotice> notices){
        sqlManager.insertBatch(SysNotice.class, notices);
    }


    public void readNotice(long uid){
        sqlManager.lambdaQuery(SysNotice.class)
            .andEq(SysNotice::getUserId, uid)
            .updateSelective(new SysNotice(){{
                setState(State.READ);
            }});
    }
}
