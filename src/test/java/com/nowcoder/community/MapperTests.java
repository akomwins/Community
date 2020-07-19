package com.nowcoder.community;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.mysql.cj.log.Log;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)
public class MapperTests {
//    通过MyBatis访问用户表，完成如下的功能：
//
//1. 根据ID查询一个用户。
//
//2. 根据用户名查询一个用户。
//
//3. 根据邮箱查询一个用户。
//
//4. 插入一个用户。
//
//5. 根据ID修改用户的状态。
//
//6. 根据ID修改用户的头像路径。
//
//7. 根据ID修改用户的密码。
    @Autowired(required = false)
    private UserMapper userMapper;
    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;
    @Test
    public void testSelectUser(){
        User user=userMapper.selectById(101);
        System.out.println(user);
        user=userMapper.selectByName("liubei");
        System.out.println(user);

        user=userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    @Test
    public void testInsertUser(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows=userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());

    }
    @Test
    public void updateUser(){
        int rows=userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows=userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows=userMapper.updatePassword(150,"aaaaaa");
        System.out.println(rows);

    }
    @Test
    public void testSelectPosts(){
        List<DiscussPost> list=discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost post:list){
            System.out.println(post);
        }
        int rows=discussPostMapper.selectDiscussPostRows(149);
        System.out.println(rows);
    }
    @Autowired(required = false)
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc",1);

        loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }
    @Autowired
    private HostHolder hostHolder;
    @Test
    public void testChangePassword(){
        User user=userMapper.selectByName("akomwins");
        String oldPassword="akomwins"+user.getSalt();
        oldPassword=CommunityUtil.md5(oldPassword);
        System.out.println(oldPassword);
        System.out.println(user.getPassword());
        System.out.println(oldPassword.equals(user.getPassword()));
    }

}
