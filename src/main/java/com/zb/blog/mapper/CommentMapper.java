package com.zb.blog.mapper;

import com.zb.blog.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentMapper {
    //查所有
    List<Comment> findCommentAll();
    //根据条件查
    List<Comment> findCommentByCondition(Map<String,Object> KV);
    //根据主键查
    List<Comment> findCommentByIds(List<Integer> ids);
    //增一条数据
    int insertComment(Comment comment);
    //增多条数据
    int insertComments(List<Comment> comments);
    //修改数据
    int updateComment(Comment comment);
    //根据主键删除数据
    int deleteCommentById(Integer id);
    //根据主键批量删除数据
    int deleteCommentByIds(List<Integer> ids);
}
