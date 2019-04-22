package com.beeasy.hzback.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.filter.AuthFilter;
import com.beeasy.mscommon.util.U;
import com.beeasy.mscommon.valid.ValidGroup;
import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.AssignID;
import org.beetl.sql.core.annotatoin.Table;

import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.beeasy.hzback.entity.SysNotice.State.READ;
import static com.beeasy.hzback.entity.SysNotice.State.UNREAD;

@Table(name = "T_SYSTEM_NOTICE")
@Getter
@Setter
public class SysNotice extends TailBean implements ValidGroup {
    @AssignID("simple")
    private Long id;
    Long fromUid;
    private Long userId;
    private Date addTime;
    private State state;
    private Type type;
    @NotBlank(message = "消息内容不能为空", groups = {Add.class, Edit.class})
    private String content;
    private String bindData;

    public enum Type {
        //系统通知
        SYSTEM,
        //工作流
        WORKFLOW,
        //台账信息
        ACC_LOAN,
        //短消息
        MESSAGE,
        //企查查通知
        QCC
    }

    public enum State {
        UNREAD,
        READ
    }

    @Override
    public void onBeforeDelete(SQLManager sqlManager, Long[] id) {
        long count = sqlManager.lambdaQuery(SysNotice.class)
            .andIn(SysNotice::getId, Arrays.asList(id))
            .andEq(SysNotice::getUserId, AuthFilter.getUid())
            .count();
        Assert(count == id.length, "你只能删除属于你的消息!");
    }


    @Override
    public String onGetOneSql(SQLManager sqlManager, Object id) {
        //只能读自己的
        Assert(sqlManager.lambdaQuery(SysNotice.class)
            .andEq(SysNotice::getUserId, AuthFilter.getUid())
            .orEq(SysNotice::getFromUid, AuthFilter.getUid())
            .count() > 0, "你无法查看别人的消息");
        return "im.查询消息";
    }

    @Override
    public String onGetListSql(Map<String,Object> params) {
        params.put("uid", AuthFilter.getUid());
        return "im.查询系统通知";
    }


    @Override
    public Object onAdd(SQLManager sqlManager) {
        List<String> uids = (List<String>) get("$uids");
        JSONArray files = (JSONArray) get("$files");
        Assert(uids.size() > 0, "请选择消息接受者");
        List<SysNotice> notices = uids.stream().map(uid -> {
            SysNotice sysNotice = new SysNotice();
            sysNotice.fromUid = AuthFilter.getUid();
            sysNotice.userId = Long.parseUnsignedLong(uid);
            sysNotice.state = UNREAD;
            sysNotice.type = Type.MESSAGE;
            sysNotice.content = content;
            sysNotice.bindData = JSON.toJSONString(files);
            sysNotice.addTime = new Date();
            return sysNotice;
        })
            .collect(Collectors.toList());
        sqlManager.insertBatch(SysNotice.class, notices);
        return true;
    }

    @Override
    public Object onExtra(SQLManager sqlManager, String action, JSONObject object) {
        switch (action){
            case "getUList":
                object.put("uid", AuthFilter.getUid());
                return U.beetlPageQuery("system.查询未读消息", JSONObject.class, object);

            //发件箱
            case "sentList":
                object.put("uid", AuthFilter.getUid());
                object.put("sent",1);
                return U.beetlPageQuery("im.查询系统通知", JSONObject.class, object);

            case "read":
                List<Long> list = U.toIdList(object.getString("id"));
                sqlManager.lambdaQuery(SysNotice.class)
                    .andIn(SysNotice::getId, list)
                    .andEq(SysNotice::getUserId, AuthFilter.getUid())
                    .updateSelective(new SysNotice(){{
                        setState(READ);
                    }});
                break;
        }
        return null;
    }
}
