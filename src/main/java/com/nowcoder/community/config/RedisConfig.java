package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
   @Bean
   public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
       //连接工厂注入,参数
       //实力话
       RedisTemplate<String,Object>template=new RedisTemplate<>();
       template.setConnectionFactory(factory);
       //设置key序列化方式
       template.setKeySerializer(RedisSerializer.string());
       //设置普通value的序列化方式
       template.setValueSerializer(RedisSerializer.json());//json容易识别
       //设置hash的key的序列化
       template.setHashKeySerializer(RedisSerializer.string());
       //设置hash的value序列化
       template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();//做完设置后生效
       return template;
   }
}
