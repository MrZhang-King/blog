package com.zb.blog.mapper;

import com.zb.blog.entity.Blog;

import java.util.List;
import java.util.Map;

public interface BlogMapper {

    //查所有
    List<Blog> findBlogAll();

    //根据条件查
    List<Blog> findBlogByCondition(Map<String,Object> kv);

    //根据主键查
    List<Blog> findBlogByIds(List<Integer> ids);

    //增一条数据
    int insertBlog(Blog blog);

    //增多条数据
    int insertBlogs(List<Blog> blogs);

    //修改数据
    int updateBlog(Blog blog);

    //根据主键删除数据
    int deleteBlogById(Integer id);

    //根据主键批量删除数据
    int deleteBlogByIds(List<Integer> ids);

    //根据主键修改博客的浏览数量
    int updateViewCount(Blog blog);
}
