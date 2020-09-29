package com.zb.blog.handler;

import com.zb.blog.entity.Sort;
import com.zb.blog.entity.Tag;
import com.zb.blog.entity.User;
import com.zb.blog.mapper.MapResultMapper;
import com.zb.blog.utils.SpringContextUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 该类负责查询标签和分类，将查询到的结果处理为map集合
 */
@Component
@DependsOn("springContextUtil")
public class TableHandler {

    //分类id和分类对象对应
    @Getter
    private static Map<Integer, Sort> sortMap;

    //标签id和标签对象对应
    @Getter
    private static Map<Integer, Tag> tagMap;

    //标签id和用户对象对应
    @Getter
    private static Map<Integer, User> userMap;

    @Autowired
    private static MapResultMapper mapResultMapper;

    static {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        mapResultMapper = (MapResultMapper) applicationContext.getBean("mapResultMapper");
        //查询分类
        sortMap = mapResultMapper.selectSortToMap();
        //查询标签
        tagMap = mapResultMapper.selectTagToMap();
        //查询用户
        userMap = mapResultMapper.selectUserToMap();

    }

    //刷新分类的方法
    public static void refreshSort(){
        sortMap = mapResultMapper.selectSortToMap();
    }

    //刷新标签的方法
    public static void refreshTag(){
        tagMap = mapResultMapper.selectTagToMap();
    }

    //刷新用户的方法
    public static void refreshUser(){
        userMap = mapResultMapper.selectUserToMap();
    }
}
