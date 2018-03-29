package com.beeasy.hzback.core.aop;

import bin.leblanc.zed.JPAUtil;
import bin.leblanc.zed.Zed;
import com.beeasy.hzback.core.helper.Result;
import javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanProperty;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class BindingResultAop {

    @Autowired
    Zed zed;
    @Autowired
    JPAUtil jpaUtil;

    @Autowired
    EntityManager entityManager;

    @Around(value ="execution(* com.beeasy.hzback.modules.*.controller..*(..)) ")
    public Object twiceAsOld(
        ProceedingJoinPoint joinPoint
    ) {

        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object[] args = joinPoint.getArgs();

        for(int i = 0; i < args.length; i++){
            Object arg = args[i];
            if(arg instanceof BindingResult){
                if(((BindingResult) arg).hasErrors()){
                    return Result.error(arg);
                }
            }

//            if(zed.getEntityMap().containsValue(arg.getClass())){
//                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//                CriteriaQuery query = cb.createQuery(args.getClass());
//                Root root = query.from(arg.getClass());
//                query.where(cb.equal(root.get(jpaUtil.getIdName(root)),
//                        (request.getParameter(methodSignature.getParameterNames()[i]))));
//                query.select(root);
//                TypedQuery q = entityManager.createQuery(query);
//                q.setMaxResults(1);
//                Object target = q.getSingleResult();
//                args[i] = target;
//            }
        }

        Result result = null;
        try {
            result = (Result) joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

}
