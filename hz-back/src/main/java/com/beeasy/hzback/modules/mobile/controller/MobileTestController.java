package com.beeasy.hzback.modules.mobile.controller;

import bin.leblanc.maho.RPCOption;
import bin.leblanc.maho.RPCall;
import com.beeasy.hzback.core.helper.Result;
import com.beeasy.hzback.modules.system.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mapi/{module}/{action}")
public class MobileTestController {

    @Autowired
    UserService userService;

    @PostConstruct
    public void init(){
        RPCOption option = new RPCOption();
        option.setReturn(obj -> {
            if(obj instanceof String){
                if(StringUtils.isEmpty((String) obj)){
                    return Result.ok();
                }
                else{
                    return Result.error((String) obj);
                }
            }
            else if(null == obj){
                return Result.error();
            }
            else if(obj instanceof Result){
                return obj;
            }
            else if(obj instanceof Optional){
                return Result.finish((Optional) obj);
            }
            else if(obj instanceof Boolean || obj.getClass().getName().equals("boolean")){
                return Result.finish((Boolean) obj);
            }
            else{
                return obj;
            }
        })
        .setDev(true);

        RPCall.register("user", userService, option);
    }

    @RequestMapping(value = "")
    public String getNoArgs(@PathVariable String module, @PathVariable String action, HttpServletRequest request){
        return call(module,action,request);
    }
    @RequestMapping(value = "/{a1}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, HttpServletRequest request) {
        return call(module,action,request,a1);
    }
    @RequestMapping(value = "/{a1}/{a2}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, @PathVariable String a2, HttpServletRequest request) {
        return call(module,action,request,a1,a2);
    }
    @RequestMapping(value = "/{a1}/{a2}/{a3}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, @PathVariable String a2, @PathVariable String a3, HttpServletRequest request) {
        return call(module,action,request,a1,a2,a3);
    }
    @RequestMapping(value = "/{a1}/{a2}/{a3}/{a4}",method = {RequestMethod.GET,RequestMethod.POST})
    public String requestArgs(@PathVariable String module, @PathVariable String action, @PathVariable String a1, @PathVariable String a2, @PathVariable String a3, @PathVariable String a4, HttpServletRequest request) {
        return call(module,action,request,a1,a2,a3,a4);
    }

    private String call(String module, String action, HttpServletRequest request, String ...args){
        String body = null;
        if(request.getMethod().equals("POST")){
        try {
            List<String> lines = org.apache.commons.io.IOUtils.readLines(request.getInputStream());
            body = StringUtils.join(lines.toArray(),"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        Object ret =  RPCall.call(module,action,args,body);
        return ret instanceof Result ? ((Result) ret).toJson() : Result.ok(ret).toJson();
    }


}
