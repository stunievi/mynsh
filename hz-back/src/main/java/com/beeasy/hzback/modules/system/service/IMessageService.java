package com.beeasy.hzback.modules.system.service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface IMessageService {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    static class UnreadNum {
        Long num;
        Long sessionId;
    }


}
