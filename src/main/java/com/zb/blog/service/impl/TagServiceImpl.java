package com.zb.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Tag;
import com.zb.blog.handler.TableHandler;
import com.zb.blog.mapper.TagMapper;
import com.zb.blog.service.TagService;
import com.zb.blog.utils.ListUtil;
import com.zb.blog.utils.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl implements TagService {

    @Value("${tagPageCount}")
    private Integer pageCount;
    @Value("${indexTagPageCount}")
    private Integer indexPageCount;
    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getTagByCondition(Map<String,Object> condition) {
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            return tagMapper.findTagAll();
        }
        return tagMapper.findTagByCondition(condition);
    }


    //获取正在使用的标签(标签状态为0是正在使用，为-1是已删除)
    @Override
    public List<Tag> getUseTag() {
        return tagMapper.findTagByCondition(MapUtil.addElement("tagState",0));
    }

    /**
     * 做分页的方法
     * @param pageNum 第几页
     * @param condition 查询条件
     *                  参数condition中包含了pageNum 这样写是为了可读性好
     * @return 查询结果
     */
    @Override
    public PageInfo<Tag> pageHelper(Integer pageNum, Map<String, Object> condition) {
        //第几页  每页显示几条数据
        PageHelper.startPage(pageNum,pageCount);
        List<Tag> tagList;
        //没有条件的时候查询所有
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            tagList = tagMapper.findTagAll();
        }else{//按照条件查
            tagList = tagMapper.findTagByCondition(condition);
        }
        return new PageInfo<>(tagList);
    }


    /**
     * 添加标签的方法
     * @param tag 标签对象
     * @return 1：成功 ：失败
     */
    @Override
    public int add(Tag tag) {
        if(ObjectUtil.isEmpty(tag)){
            //失败
            return 0;
        }
        int result = tagMapper.insertTag(tag);
        if(result == 1){
            TableHandler.refreshTag();
        }
        return result;
    }

    /**
     * 删除标签的方法（真实操作是将标签的状态改为-1）
     * @param id 标签的id
     * @return 0：删除失败 1：删除成功
     */
    @Override
    public int delete(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return 0;
        }
        //根据id查询该标签
        List<Tag> tagList = tagMapper.findTagByIds(ListUtil.addElement(id));
        //如果没有查到，返回失败
        if(ObjectUtil.isEmpty(tagList) || tagList.size() == 0){
            return 0;
        }
        Tag tag = tagList.get(0);
        //设置标签的状态和更改时间
        tag.setTagState(-1);
        tag.setUpdateTime(new Date());
        //修改数据库中该标签
        int result = tagMapper.updateTag(tag);
        if(result == 1){
            //刷新缓存中的数据(直接查数据库浪费性能)
            //可通过修改缓存map中的数据保证数据的一致性
            TableHandler.getTagMap().put(tag.getId(),tag);
        }
        return result;
    }

    /**
     * 根据标签id查询分类
     * @param id 标签id
     * @return 查询到的标签  null:没有查到
     */
    @Override
    public Tag getTagById(Integer id) {
        List<Tag> tagList =  tagMapper.findTagByIds(ListUtil.addElement(id));
        if(ObjectUtil.isEmpty(tagList) || tagList.size() == 0){
            return null;
        }
        return tagList.get(0);
    }

    /**
     * 编辑标签（修改标签）
     * @param tag 修改过的标签
     * @return 1：成功 0：失败
     */
    @Override
    public Integer edit(Tag tag) {
        if(ObjectUtil.isEmpty(tag)){
            return 0;
        }
        int result = tagMapper.updateTag(tag);
        if(result == 1){
            //刷新缓存中的数据(直接查数据库浪费性能)
            //可通过修改缓存map中的数据保证数据的一致性
//            SortAndTagHandler.refreshTag();
            TableHandler.getTagMap().put(tag.getId(),tag);
        }
        return result;
    }

    //==========================================

    /**
     * 查询所有正在使用中的标签并进行排序和分页
     * @param descOrAsc 升序/降序
     * @param pageNum 要第几页的数据
     * @return pageInfo对象
     */
    @Override
    public PageInfo<Tag> orderTag(String descOrAsc, Integer pageNum) {
        if(ObjectUtil.isEmpty(pageNum) || StrUtil.isEmpty(descOrAsc)){
            return null;
        }
        PageHelper.startPage(pageNum,indexPageCount);
        List<Tag> tagList = tagMapper.orderTag(descOrAsc);
        return new PageInfo<>(tagList);
    }

}
