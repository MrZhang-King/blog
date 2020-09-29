package com.zb.blog.mapper;

import com.zb.blog.entity.Tag;

import java.util.List;
import java.util.Map;

public interface TagMapper {
    //查所有
    List<Tag> findTagAll();
    //根据条件查
    List<Tag> findTagByCondition(Map<String,Object> KV);
    //根据主键查
    List<Tag> findTagByIds(List<Integer> ids);
    //增一条数据
    int insertTag(Tag tag);
    //增多条数据
    int insertTags(List<Tag> tags);
    //修改数据
    int updateTag(Tag tag);
    //根据主键删除数据
    int deleteTagById(Integer id);
    //根据主键批量删除数据
    int deleteTagByIds(List<Integer> ids);

    //=============================

    List<Tag> orderTag(String descOrAsc);
}
