package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import io.lettuce.core.RedisURI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private  static final Logger logger= LoggerFactory.getLogger(LoginController.class);
    @Autowired //注入
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @RequestMapping(path="/register",method = RequestMethod.GET)

    public String getRegisterPage(){

         return "/site/register";
    }
    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage(){

        return "/site/login";
    }
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @RequestMapping(path="/register",method=RequestMethod.POST)
    public String register(Model model, User user){//model存数据
        Map<String,Object> map=userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功,请到邮箱进行账号激活");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    @RequestMapping(path="/activation/{userId}/{code}",method = RequestMethod.GET)
    //从路径取值,@Pathvariable
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code){
        int result=userService.activation(userId,code);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功,您的账号已可以正常使用!");
            model.addAttribute("target","/login");

        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作,该账号已被激活!");
            model.addAttribute("target","/index");
        }
        else{
            model.addAttribute("msg","激活失败!");
            model.addAttribute("target","/index");
        }
    return "/site/operate-result";
    }
//    public void getKaptcha(HttpServletResponse response, HttpSession session){
//        //生成验证码
//        String text=kaptchaProducer.createText();
//        BufferedImage image=kaptchaProducer.createImage(text);
//        //将验证码存入session
//        session.setAttribute("kaptcha",text);
//        //将图片输出给浏览器
//        response.setContentType("image/png");
//        try {
//            OutputStream os=response.getOutputStream();
//            ImageIO.write(image,"png",os);
//        } catch (IOException e) {
//            logger.error("响应验证码失败:"+e.getMessage());
//        }
//    }
    @RequestMapping(path="/kaptcha",method=RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response){
        //生成验证码
        String text=kaptchaProducer.createText();
        BufferedImage image=kaptchaProducer.createImage(text);
        //验证码的归属
        //给用户临时给一个凭证
        String kaptchaOwner= CommunityUtil.generateUuid();
        Cookie cookie=new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //将验证码存入redis
        String redisKey= RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        //60s后失效
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os=response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            logger.error("响应验证码失败:"+e.getMessage());
        }


    }
    @RequestMapping(path="/login",method=RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme,
                        Model model,/*HttpSession session,*/HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner){
        //取验证码
        //String kaptcha=(String)session.getAttribute("kaptcha");
        String kaptcha=null;
        if(StringUtils.isNoneBlank(kaptchaOwner)){
            String redisKey=kaptcha= RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha=(String)redisTemplate.opsForValue().get(redisKey);

        }
        if(StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){//忽略大小写

            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";

        }
        //检查账号密码
        int expireSeconds=rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object>map=userService.login(username,password,expireSeconds);
        if(map.containsKey("ticket")){
            //创建一个cookie保存ticket到客户端
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expireSeconds);
            //发送给页面
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }



    }

    //springmvc注入cookie
    @RequestMapping(path="/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        SecurityContextHolder.clearContext();
        return "redirect:/login";

    }

}
