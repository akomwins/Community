package com.nowcoder.community.controller;

import com.mysql.cj.log.Log;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.catalina.Host;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @RequestMapping(path="/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }
    @RequestMapping(path="/uploadPassword",method = RequestMethod.POST)
    public String uploadPassword(String oldPassword,String newPassword,Model model){

        User user=hostHolder.getUser();
        System.out.println(CommunityUtil.md5(oldPassword+user.getSalt()));
        System.out.println(user.getPassword());
        if(oldPassword==null){
            model.addAttribute("error","未输入密码!");
            return "/site/setting";
        }


        else if(!CommunityUtil.md5(oldPassword+user.getSalt()).equals(user.getPassword())){
            model.addAttribute("error","不符合原始密码");
            return "/site/setting";
        }
        //更新用户密码
        if(newPassword.length()<8){
            model.addAttribute("error","密码不能少于八位");
            return "/site/setting";
        }
        else {
            newPassword = CommunityUtil.md5(newPassword + user.getSalt());
            userService.updatePassword(user.getId(), newPassword);
            return "redirect:/index";
        }
    }
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","还未选择图片");
            return "/site/setting";
        }
        String fileName=headerImage.getOriginalFilename();//获取原始文件名
        String suffix=fileName.substring(fileName.lastIndexOf("."));//从最后一个.的位置往后截取
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","图片格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        fileName=CommunityUtil.generateUuid()+suffix;
        //确定文件存放路径
        File dest=new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:"+e.getMessage());
            throw  new RuntimeException("上传文件失败,服务器发生异常!"+e);
        }
        //更新用户头像路径(web访问路径)
        //http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);
        return "redirect:/index";

    }
    @RequestMapping(path="/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //找到服务器存在路径
        fileName=uploadPath+"/"+fileName;
        //文件猴嘴
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try(
                // 自动关闭
                FileInputStream fis=new FileInputStream(fileName);
                OutputStream os=response.getOutputStream();


        ) {
            byte[] buffer=new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }
    }

}
