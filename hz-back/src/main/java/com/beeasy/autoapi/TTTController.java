package com.beeasy.autoapi;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.mscommon.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/aaa")
@RestController
public class TTTController {

    @RequestMapping("/ff")
    public Result ttt(
            @RequestBody JSONObject object
            ){
        return Result.ok();
    }
}
