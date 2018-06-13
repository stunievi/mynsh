package com.beeasy.hzback.modules.mobile.controller;

import bin.leblanc.maho.RPCall;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Configuration
@RestController
@RequestMapping("/mapi/{module}/{action}")
public class MobileTestController {

    @PostConstruct
    public void init(){
        RPCall.register("test",new Object(){
            public String fuck(int a, long b){
                return a + b + "";
            }
        });
    }

    @GetMapping(value = "")
    public String getNoArgs(@PathVariable String module, @PathVariable String action){
        return module + action;
    }
    @PostMapping("")
    public String postNoArgs(){
        return "ok";
    }

    @RequestMapping(value = "/{a1}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, HttpServletRequest request) {
        Object result = call(module,action,request,a1);
        return module + " " + action + " " + a1 + result.toString();
    }
    @RequestMapping(value = "/{a1}/{a2}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, @PathVariable String a2, HttpServletRequest request) {
        Object result = call(module,action,request,a1,a2);
        return module + " " + action + " " + a1 + a2 + result.toString();
    }
    @RequestMapping(value = "/{a1}/{a2}/{a3}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, @PathVariable String a2, @PathVariable String a3, HttpServletRequest request) {
        Object result = call(module,action,request,a1,a2,a3);
        return module + " " + action + " " + a1 + a2 + a3 + result.toString();
    }


    private Object call(String module, String action, HttpServletRequest request, String ...args){
        String body = null;
        try {
            List<String> lines = org.apache.commons.io.IOUtils.readLines(request.getInputStream());
            body = StringUtils.join(lines.toArray(),"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return RPCall.call(module,action,args,body);
    }


}
