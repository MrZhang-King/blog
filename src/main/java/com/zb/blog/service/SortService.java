package com.zb.blog.service;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Sort;

import java.util.List;
import java.util.Map;

public interface SortService {

    List<Sort> getSortByCondition(Map<String,Object> condition);

    PageInfo<Sort> pageHelper(Integer pageNum, Map<String,Object> condition);

    int delete(Integer id);

    int add(Sort sort);

    Sort getSortById(Integer id);

    Integer edit(Sort sort);

    List<Sort> getUseSort();

    //==============前台页面===================

    PageInfo<Sort> orderSort(String descOrAsc, Integer pageNum);
}
