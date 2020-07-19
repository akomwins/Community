package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUuid(){
        return UUID.randomUUID().toString().replaceAll("-","");

    }
    //MD5加密
    //hello->asdasdasdasd
    //hello+3ea4sd8->asdsadasdasd
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        else
            return DigestUtils.md5DigestAsHex(key.getBytes());//加密成16进制


    }

}
