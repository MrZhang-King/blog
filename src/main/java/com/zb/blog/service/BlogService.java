package com.zb.blog.service;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BlogService {
    List<Blog> selectAll();

    int blogAdd(Blog blog, MultipartFile blogImgFile);

    PageInfo<Blog> pageHelper(Integer pageNum, Integer pageCount, Map<String,Object> condition);

    int delete(Integer id);

    Blog getBlogById(Integer id);

    Integer edit(Blog blog);

    //==============前台页面===================


    List<Blog> selectByCondition(Map<String,Object> condition);

    Blog setPropertyObj(Blog blog);

    int addBlogViewCount(Blog blog);
}
