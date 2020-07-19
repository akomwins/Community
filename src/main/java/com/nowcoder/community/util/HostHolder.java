package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;
//作为容器，持有用户信息，用于代替session
@Component
public class HostHolder {
    private ThreadLocal<User> users=new ThreadLocal<>();
    public void setUsers(User user){
        users.set(user);

    }
    public User getUser(){
        return users.get();
    }
    //清理
    public void clear(){
        users.remove();
    }
}
