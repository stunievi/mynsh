package com.beeasy.hzback.api.controller;


import bin.leblanc.zed.Result;
import com.beeasy.hzback.modules.setting.dao.IUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class UserAPI {

    @Autowired
    IUserDao userDao;

    @RequestMapping("/users")
    public Result list(
            @PageableDefault(value = 15, sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ){
        return Result.ok(userDao.findAll(pageable));
    }
}
