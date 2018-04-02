package com.beeasy.hzback.api.controller;


import bin.leblanc.zed.Zed;
import bin.leblanc.zed.exception.NoMethodException;
import bin.leblanc.zed.exception.NoPermissionException;
import com.beeasy.hzback.core.helper.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class CommonController {



    @Autowired
    Zed zed;

    @RequestMapping("/zed")
    public Result menu2(
            HttpServletRequest request,
            @RequestBody String body
    ){
        log.info(body);
        try{
            return Result.ok(zed.parse(body));
        }
        catch (NoMethodException e){
            return Result.error("error method");
        }
        catch (NoPermissionException e){
            return Result.error("permission error");
        }
        catch (Exception e){
            return Result.error();
        }
    }
}
