package com.zb.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Sort;
import com.zb.blog.handler.TableHandler;
import com.zb.blog.mapper.SortMapper;
import com.zb.blog.service.SortService;
import com.zb.blog.utils.ListUtil;
import com.zb.blog.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SortServiceImpl implements SortService {

    @Value("${sortPageCount}")
    private Integer pageCount;
    @Value("${indexSortPageCount}")
    private Integer indexPageCount;

    @Autowired
    private SortMapper sortMapper;

    @Override
    public List<Sort> getSortByCondition(Map<String,Object> condition) {
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            return sortMapper.findSortAll();
        }
        sortMapper.findSortByCondition(condition);
        return sortMapper.findSortAll();
    }

    /**
     * 做分页的方法
     * @param pageNum 第几页
     * @param condition 查询条件
     *                  参数condition中包含了pageNum 这样写是为了可读性好
     * @return 查询结果
     */
    @Override
    public PageInfo<Sort> pageHelper(Integer pageNum, Map<String, Object> condition) {
        if(ObjectUtil.isEmpty(pageNum)){
            return null;
        }
        //第几页  每页显示几条数据
        PageHelper.startPage(pageNum,pageCount);
        List<Sort> sortList;
        //没有条件的时候查询所有
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            sortList = sortMapper.findSortAll();
        }else{//按照条件查
            sortList = sortMapper.findSortByCondition(condition);
        }
        return new PageInfo<>(sortList);
    }

    /**
     * 删除分类的方法（真实操作是将分类的状态改为-1）
     * @param id 分类的id
     * @return 0：删除失败 1：删除成功
     */
    @Override
    public int delete(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return 0;
        }
        //根据id查询该分类
        List<Sort> sortList = sortMapper.findSortByIds(ListUtil.addElement(id));
        //如果没有查到，返回失败
        if(ObjectUtil.isEmpty(sortList) || sortList.size() == 0){
            return 0;
        }
        Sort sort = sortList.get(0);
        //设置分类的状态和更改时间
        sort.setSortState(-1);
        sort.setUpdateTime(new Date());
        //修改数据库中该分类
        int result = sortMapper.updateSort(sort);
        //刷新缓存中的数据(直接查数据库浪费性能)
        //可通过修改缓存map中的数据保证数据的一致性
        if(result == 1){
            TableHandler.getSortMap().put(sort.getId(),sort);
//            SortAndTagHandler.refreshSort();
        }
        return result;
    }

    /**
     * 添加分类的方法
     * @param sort 分类对象
     * @return 1：成功 ：失败
     */
    @Override
    public int add(Sort sort) {
        if(ObjectUtil.isEmpty(sort)){
            //失败
            return 0;
        }
        int result = sortMapper.insertSort(sort);
        //刷新缓存中的数据
        if(result == 1) {
            TableHandler.refreshSort();
        }
        return result;
    }


    /**
     * 根据分类id查询分类
     * @param id 分类id
     * @return 查询到的分类  null:没有查到
     */
    @Override
    public Sort getSortById(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return null;
        }
        List<Sort> sortList =  sortMapper.findSortByIds(ListUtil.addElement(id));
        if(ObjectUtil.isEmpty(sortList) || sortList.size() == 0){
            return null;
        }
        return sortList.get(0);
    }

    /**
     * 编辑分类（修改分类）
     * @param sort 修改过的分类
     * @return 1：成功 0：失败
     */
    @Override
    public Integer edit(Sort sort) {
        if(ObjectUtil.isEmpty(sort)){
            return 0;
        }
        int result = sortMapper.updateSort(sort);
        if(result == 1){
            //刷新缓存中的数据(直接查数据库浪费性能)
            //可通过修改缓存map中的数据保证数据的一致性
//        SortAndTagHandler.refreshSort();
            TableHandler.getSortMap().put(sort.getId(),sort);
        }

        return result;
    }


    /**
     * 获取正在使用中的分类（未删除的）
     * @return 所有正在使用的分类
     */
    @Override
    public List<Sort> getUseSort() {
        return sortMapper.findSortByCondition(MapUtil.addElement("sortState",0));
    }


    //==============================================

    /**
     * 查询所有正在使用中的分类并进行排序和分页
     * @param descOrAsc 升序/降序
     * @param pageNum 要第几页的数据
     * @return pageInfo对象
     */
    @Override
    public PageInfo<Sort> orderSort(String descOrAsc, Integer pageNum) {
        if(ObjectUtil.isEmpty(pageNum) || StrUtil.isEmpty(descOrAsc)){
            return null;
        }
        PageHelper.startPage(pageNum,indexPageCount);
        List<Sort> sortList = sortMapper.orderSort(descOrAsc);
        return new PageInfo<>(sortList);
    }


}
