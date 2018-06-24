//package com.beeasy.hzback.core.aop;
//
//import bin.leblanc.zed.JPAUtil;
//import bin.leblanc.zed.Zed;
//import com.alibaba.fastjson.JSON;
//import com.beeasy.hzback.core.exception.RestException;
//import com.beeasy.hzback.core.helper.Result;
//import com.beeasy.hzback.modules.system.dao.IUserAllowApiDao;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.persistence.EntityManager;
//import javax.servlet.http.HttpServletRequest;
//
//@Aspect
//@Component
//public class MethodPermissionAop {
//
//    @Autowired
//    EntityManager entityManager;
//    @Autowired
//    IUserAllowApiDao allowApiDao;
//
////    static String SUCCESS = JSON.toJSONString(Result.ok());
////    static String ERROR = JSON.toJSONString(Result.error());
//
//    @Pointcut(value = "execution(* com.beeasy.hzback.modules.*.controller..*(..)) ")
//    public void point(){}
//
//    @Around(value ="point()")
//    public Object twiceAsOld(
//        ProceedingJoinPoint joinPoint
//    ) {
//
////        Signature signature = joinPoint.getSignature();
////        MethodSignature methodSignature = (MethodSignature) signature;
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String url = request.getRequestURI();
//        int a = 1;
//
//
////            if(zed.getEntityMap().containsValue(arg.getClass())){
////                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
////                CriteriaQuery query = cb.createQuery(args.getClass());
////                Root root = query.from(arg.getClass());
////                query.where(cb.equal(root.get(jpaUtil.getIdName(root)),
////                        (request.getParameter(methodSignature.getParameterNames()[i]))));
////                query.select(root);
////                TypedQuery q = entityManager.createQuery(query);
////                q.setMaxResults(1);
////                Object target = q.getSingleResult();
////                args[i] = target;
////            }
////        }
//
//        try {
//            return joinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        return null;
//    }
//
//
//
////    @AfterReturning(pointcut = "point()",returning = "rtv")
////    public void returns(JoinPoint joinPoint, Boolean rtv){
////        joinPoint
////        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
////        try {
////            response.setStatus(200);
////            response.getWriter().write(123312);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////    }
//
//
//}
