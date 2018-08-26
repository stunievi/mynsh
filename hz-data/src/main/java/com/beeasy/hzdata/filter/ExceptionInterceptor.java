package com.beeasy.hzdata.filter;

import act.app.ActionContext;
import act.handler.builtin.controller.ActionHandlerInvoker;
import org.osgl.http.H;
import org.osgl.mvc.result.ErrorResult;
import org.osgl.mvc.result.Result;

public class ExceptionInterceptor extends act.handler.builtin.controller.ExceptionInterceptor {
    @Override
    protected Result internalHandle(Exception e, ActionContext actionContext) throws Exception {
        System.out.println("fuck");
        return new ErrorResult(H.Status.OK,"rilegou");
    }

    @Override
    public void accept(ActionHandlerInvoker.Visitor visitor) {

    }

    @Override
    public boolean sessionFree() {
        return false;
    }

    @Override
    public boolean express() {
        return false;
    }

    @Override
    public boolean skipEvents() {
        return false;
    }
}
