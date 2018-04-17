package com.beeasy.hzback.modules.system.response;

import com.beeasy.hzback.modules.setting.entity.UserProfile;
import com.beeasy.hzback.modules.system.entity.Department;
import lombok.Data;

@Data
public class UserInfoResponse{

//    private Role[] rs;
    private Department[] ds;
    private UserProfile profile;

//    private Message message;
//
//    @Data
//    static class Message{
//    }
//
//    protected UserInfoResponse(boolean success, Object item) {
//        super(success, item);
//    }
//
//    protected UserInfoResponse(boolean success) {
//        super(success);
//    }



}
