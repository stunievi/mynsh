package com.beeasy.hzback.modules.system.response;

import com.beeasy.hzback.modules.system.entity_kt.Department;
import com.beeasy.hzback.modules.system.entity_kt.User;
import com.beeasy.hzback.modules.system.entity_kt.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse{

    String token;
    User user;

    //    private Role[] rs;
    private Department[] ds;
    private UserProfile profile;

    public UserInfoResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

//    private Role[] rs;
//    private Department[] ds;
//    private UserProfile profile;

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
