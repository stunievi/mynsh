package com.beeasy.hzreport.filter;//package com.beeasy.hzdata.filter;
//
//import act.ActResponse;
//import act.app.ActionContext;
//import act.controller.Controller;
//import act.handler.builtin.controller.BeforeInterceptor;
//import act.plugin.Plugin;
//import act.security.CORS;
//import org.osgl.http.H;
//import org.osgl.mvc.annotation.Before;
//import org.osgl.mvc.result.Result;
//
//import javax.inject.Singleton;
//
//@Singleton
//public class CrossDomainFilter extends BeforeInterceptor{
//
//    public CrossDomainFilter() {
//        super(1);
//        Plugin.InfoRepo.register(this);
//    }
//
//    @Override
//    public Result handle(ActionContext actionContext) throws Exception {
//        System.out.println("fuck");
//        return null;
//    }
//
//    @Override
//    public boolean sessionFree() {
//        return false;
//    }
//
//    @Override
//    public boolean express() {
//        return false;
//    }
//
//    @Override
//    public boolean skipEvents() {
//        return false;
//    }
//
//    @Override
//    public CORS.Spec corsSpec() {
//        return null;
//    }
//}
