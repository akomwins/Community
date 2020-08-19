package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit,int orderMode);

    //Param注解用于给参数取别名，如果方法只有一个参数并且在<if>中使用必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    //增加查询帖子详情方法
    DiscussPost selectDiscussPostById(int id);
    //更新评论数量
    int updateCommentCount(int id,int commentCount);
    //更新状态
    int updateType(int id,int type);
    //
    int updateStatus(int id,int status);
    //
    int updateScore(int id,double score);



}
