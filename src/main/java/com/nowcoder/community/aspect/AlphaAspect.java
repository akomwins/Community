package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")//service所有的类所有的方法所有的参数
    public void pointcut(){

    }
    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }
    @After("pointcut()")
    public void after(){

        System.out.println("after");
    }
    //返回值以后再处理
    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterreturing");
    }
    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("before");

    }
    //前面和后面都植入
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object object=joinPoint.proceed();//调用目标组建方法
        System.out.println("around after");

        return object;

    }
}
