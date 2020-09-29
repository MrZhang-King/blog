package com.zb.blog.service;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Tag;

import java.util.List;
import java.util.Map;

public interface TagService {

    List<Tag> getTagByCondition(Map<String,Object> condition);

    List<Tag> getUseTag();

    PageInfo<Tag> pageHelper(Integer pageNum, Map<String,Object> condition);

    int add(Tag tag);

    int delete(Integer id);

    Tag getTagById(Integer id);

    Integer edit(Tag tag);

    //==============前台页面===================

    PageInfo<Tag> orderTag(String descOrAsc, Integer pageNum);


}
