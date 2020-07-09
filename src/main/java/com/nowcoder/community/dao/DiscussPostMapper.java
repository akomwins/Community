package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);

    //Param注解用于给参数取别名，如果方法只有一个参数并且在<if>中使用必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);







}
