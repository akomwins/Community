package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CookieUtil {
    public static  String getValue(HttpServletRequest request, String name){
        if(request==null||name==null){
            throw new IllegalArgumentException("参数为空!");
        }
        Cookie[] cookies=request.getCookies();
        if(cookies!=null){
            //遍历cookie判断key是否等于name
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }

            }
        }
        return null;

    }
}
