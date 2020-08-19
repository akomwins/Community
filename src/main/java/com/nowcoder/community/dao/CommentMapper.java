package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);
    //查询一共有多少条数据
    int selectCountByEntity(int entityType,int entityId);
    //增加评论
    int insertComment(Comment comment);
    Comment selectCommentById(int id);

}
