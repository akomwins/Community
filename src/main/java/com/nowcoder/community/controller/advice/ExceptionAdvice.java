package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@ControllerAdvice(annotations = Controller.class)//这个组件只去扫描带有controller的bean
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);


    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse) throws IOException {
        logger.error("服务器发生异常"+e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            logger.error(element.toString());

        }
        //普通请求或异步请求哦按段
        String xRequestedWith=httpServletRequest.getHeader("x-requested-with");
        //异步请求
        if("XMLHttpRequest".equals(xRequestedWith)){
            httpServletResponse.setContentType("application/plain;charset=utf-8");
            PrintWriter writer=httpServletResponse.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));

        }else{
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/error");
        }


    }
}
