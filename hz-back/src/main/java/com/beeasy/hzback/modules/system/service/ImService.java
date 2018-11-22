package com.beeasy.hzback.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.core.helper.Utils;
import com.beeasy.hzback.entity.*;
import com.beeasy.mscommon.entity.*;
import com.beeasy.hzback.view.DepartmentUser;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.engine.PageQuery;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.S;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImService {

    @Value("${uploads.server}")
    String UPLOAD_SERVER;

    @Autowired
    SQLManager sqlManager;
    @Autowired
    FileService fileService;

    public Map init(long uid){
        List<DepartmentUser> dulist = sqlManager.query(DepartmentUser.class)
            .select();
        User user = sqlManager.unique(User.class, uid);
//        Map map = C.newMap();
        List list = C.newList(
            C.newMap(
                "id",-1
                , "groupname", "我的好友"
                , "list", dulist.stream().map(du -> C.newMap(
                    "username", du.getTrueName()
                    , "id", du.getUid()
                    , "status", "online"
                    , "avatar", S.fmt("%s/file/avatar/%d.jpg", UPLOAD_SERVER, du.getUid())
                    , "sign", S.fmt("%s - %s", du.getDname(), du.getQname())
                )).collect(Collectors.toList())
            )
        );
//        for (DepartmentUser departmentUser : dulist) {
//            if(!map.containsKey(departmentUser.getDid())){
//                JSONObject object = new JSONObject();
//                object.put("id", departmentUser.getDid());
//                object.put("groupname", departmentUser.getDname());
//                object.put("list", new JSONArray());
//                list.add(object);
//                map.put(departmentUser.getDid(), object);
//            }
//            JSONObject object = (JSONObject) map.get(departmentUser.getDid());
//            object.getJSONArray("list").add(new JSONObject(){{
//                put("username", departmentUser.getTrueName());
//                put("id", departmentUser.getUid());
//                put("status","online");
//                put("avatar", S.fmt("/file/avatar/%s", departmentUser.getUid() + ".jpg"));
//            }});
//        }
        return C.newMap(
                "mine", C.newMap(
                    "username", user.getTrueName(),
                    "id", user.getId(),
                    "status", "online"
                    ,"avatar", S.fmt("%s/file/avatar/%d.jpg", UPLOAD_SERVER ,user.getId())
                ),
                "friend", list
        );
    }


    public Msg sendMessage(long fromUid, long toUid, String content, MultipartFile file){
        Msg msg = new Msg();
        msg.setFromType(Msg.UserType.USER);
        msg.setToType(Msg.UserType.USER);
        msg.setFromId(fromUid);
        msg.setToId(toUid);
        msg.setSendTime(new Date());
        msg.setType(Msg.Type.TEXT);
        if($.isNotNull(file)){
            content = null;
            try {
                content = (String) fileService.uploadFile(file, SystemFile.Type.MESSAGE, null, null, null, false);
                msg.setContent(content);
                msg.setType(Msg.Type.FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            msg.setContent(content);
        }
        sqlManager.insert(msg,true);

        //写入发件人已读
//        int count = sqlManager.lambdaQuery(MsgRead.class)
//            .andEq(MsgRead::getUserId, msg.getFromId())
//            .andEq(MsgRead::getToType, msg.getToType())
//            .andEq(MsgRead::getToId, msg.getToId())
//            .updateSelective(new MsgRead(){{
//                setUnreadNum(0);
//            }});
//        //更新失败
//        if(0 == count){
//            MsgRead msgRead = new MsgRead();
//            msgRead.setUserId(msg.getFromId());
//            msgRead.setToType(msg.getToType());
//            msgRead.setToId(msg.getToId());
//            msgRead.setUnreadNum(0);
//            sqlManager.insert(msgRead);
//        }

        readMessages(msg.getFromId(), C.newList(msg.getToId()));

        //写入收件人已读
        int count = sqlManager.update("im.未读消息数递增", C.newMap("uid", msg.getToId(), "totype", msg.getFromType(), "toid", msg.getFromId()));
        if(0 == count){
            MsgRead msgRead = new MsgRead();
            msgRead.setUserId(msg.getToId());
            msgRead.setToType(msg.getFromType());
            msgRead.setToId(msg.getFromId());
            msgRead.setUnreadNum(1);
            sqlManager.insert(msgRead);
        }
        return msg;
    }

    public PageQuery getChatLogList(long uid, long toUid, BeetlPager beetlPager) {
        readMessages(uid, C.newList(toUid));
        return (Utils.beetlPageQuery("im.查询聊天记录", JSONObject.class, C.newMap("uid", uid, "touid", toUid), beetlPager));
    }

    public void readMessages(long uid, Collection<Long> toUids){
        sqlManager.lambdaQuery(MsgRead.class)
            .andEq(MsgRead::getUserId, uid)
            .andEq(MsgRead::getToType, Msg.UserType.USER)
            .andIn(MsgRead::getToId, toUids)
            .updateSelective(new MsgRead(){{
                setUnreadNum(0);
            }});
    }

    public JSONObject uploadFile(long uid, MultipartFile file){
        JSONObject object = new JSONObject();
        try {
            String name = (String) fileService.uploadFile(file, SystemFile.Type.MESSAGE,null, null,null, false);
            object.put("code", 0);
            object.put("message", "");
            object.put("data", C.newMap(
                "src", S.fmt("%s/file/%s", UPLOAD_SERVER, name)
            ));
        } catch (IOException e) {
            e.printStackTrace();
            object.put("code", -1);
            object.put("message", "上传失败");
        }
        return object;
    }
}
