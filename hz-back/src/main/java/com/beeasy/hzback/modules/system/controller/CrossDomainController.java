package com.beeasy.hzback.modules.system.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api(tags = "跨域API",description = "用于给IE10以下的浏览器提供跨域支持",hidden = true)
@Controller
public class CrossDomainController {
    @GetMapping("/open/cross_domain")
    public void test(HttpServletResponse response){
        try {
            response.setHeader("content-type","text/html");
            String s = ("<script>window.postMessage(\"fuck\")</script>");
            response.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
