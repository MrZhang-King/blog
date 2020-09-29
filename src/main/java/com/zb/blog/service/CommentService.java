package com.zb.blog.service;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Comment;

import java.util.List;
import java.util.Map;

public interface CommentService {

    PageInfo<Comment> pageHelper(Integer pageNum, Map<String, Object> condition);

    int delete(Integer id);

    Comment getCommentById(Integer id);

    int auditCommentAndAlterState(Integer id);

    //==============

    List<Comment> selectByCondition(Map<String,Object> condition);

    Map<Comment,List<Comment>> getCommentsByBlog(Blog blog);

    Map<Comment,List<Comment>> getCommentsByBlog(Integer blogId);


    int addComment(Comment comment);
}
