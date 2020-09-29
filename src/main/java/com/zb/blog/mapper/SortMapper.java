package com.zb.blog.mapper;

import com.zb.blog.entity.Sort;

import java.util.List;
import java.util.Map;

public interface SortMapper {
    //查所有
    List<Sort> findSortAll();
    //根据条件查
    List<Sort> findSortByCondition(Map<String,Object> KV);
    //根据主键查
    List<Sort> findSortByIds(List<Integer> ids);
    //增一条数据
    int insertSort(Sort sort);
    //增多条数据
    int insertSorts(List<Sort> sorts);
    //修改数据
    int updateSort(Sort sort);
    //根据主键删除数据
    int deleteSortById(Integer id);
    //根据主键批量删除数据
    int deleteSortByIds(List<Integer> ids);

    //=============================

    List<Sort> orderSort(String descOrAsc);
}
