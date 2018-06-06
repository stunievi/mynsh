package com.beeasy.hzback.modules.mobile.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/error")
@RestController
public class CustomErrorController implements ErrorController{

    private static final String PATH = "/error/error";


    @Override
    public String getErrorPath() {
        return PATH;
    }

//    @RequestMapping
//    public String error() {
//        return getErrorPath();
//    }

    @RequestMapping("/error")
    public String fuck(){
        return "23";
    }

//    @RequestMapping(value = PATH,  produces = {MediaType.APPLICATION_JSON_VALUE})
//    StatefulBody error(HttpServletRequest request, HttpServletResponse response) {
//        if(!EnvironmentUtils.isProduction()) {
//            return buildBody(request,true);
//        }else{
//            return buildBody(request,false);
//        }
//    }
}
