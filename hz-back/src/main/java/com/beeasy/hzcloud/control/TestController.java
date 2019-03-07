package com.beeasy.hzcloud.control;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping
@RestController
public class TestController {

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
}
