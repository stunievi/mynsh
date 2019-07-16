package com.beeasy.hzcloud.control;

import com.alibaba.fastjson.JSONObject;
import com.beeasy.hzback.modules.system.service.TaskSyncService;
import com.beeasy.mscommon.util.U;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RequestMapping
@RestController
public class TestController {
    @Autowired
    SQLManager sqlManager;

    @RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public String test() throws JsonProcessingException {
        JSONObject object = new JSONObject();
        object.put("code", "10000");
        object.put("message", "");
        object.put("serviceTime", (new Date().getTime()));
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(object);
        return str;
    }


    @RequestMapping(value = "/test2")
    public void t2(String table) throws IOException {
        U.getBean(TaskSyncService.class).syncDataToDataPool(table);
    }
}
