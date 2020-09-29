package com.zb.blog.mapper;

import com.zb.blog.entity.User;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    //查所有
    List<User> findUserAll();
    //根据条件查
    List<User> findUserByCondition(Map<String,Object> KV);
    //根据主键查
    List<User> findUserByIds(List<Integer> ids);
    //增一条数据
    int insertUser(User user);
    //增多条数据
    int insertUsers(List<User> users);
    //修改数据
    int updateUser(User user);
    //根据主键删除数据
    int deleteUserById(Integer id);
    //根据主键批量删除数据
    int deleteUserByIds(List<Integer> ids);
}
