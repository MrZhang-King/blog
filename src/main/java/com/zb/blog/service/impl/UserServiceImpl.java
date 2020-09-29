package com.zb.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.zb.blog.entity.User;
import com.zb.blog.handler.TableHandler;
import com.zb.blog.mapper.UserMapper;
import com.zb.blog.service.UserService;
import com.zb.blog.utils.Verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Verify verify;


    /**
     *登录验证
     * @param name 账号
     * @param password 密码
     * @return 查询到的user对象  null：登录失败
     */
    @Override
    public User login(String name, String password) {
        if(StrUtil.isEmpty(name) || StrUtil.isEmpty(password)){
            return null;
        }
        //查询条件
        Map<String,Object> condition = new HashMap<>();
        condition.put("username",name);
        condition.put("password",SecureUtil.md5(password));
        //调用mapper进行查询
        List<User> users = userMapper.findUserByCondition(condition);
        //没有查到符合条件的user
        if(ObjectUtil.isEmpty(users) || users.size() == 0){
            return null;
        }
        return users.get(0);
    }


    /**
     * 注册验证
     * @param icon 用户头像
     * @param name 用户名
     * @param managerState 用户是否是管理员
     * @param password 密码
     * @return 0：用户名或密码不能为空  1：成功  2：头像文件格式不正确（jpg/png）
     */
    @Override
    public Integer register(MultipartFile icon, Integer managerState, String name, String password) {
        //判断参数是否有效
        if(ObjectUtil.isEmpty(icon) || StrUtil.isEmpty(name) || StrUtil.isEmpty(password)){
            return 0;
        }
        //获取文件名
        String fileName = icon.getOriginalFilename();
        //验证文件格式
        if(!verify.verifyIcon(fileName)){
            return 2;
        }
        //文件后缀
        String fileSuffix = fileName.substring(fileName.indexOf("."));
        //存储路径
//        static/userIcon/张斌1_icon.jpg
        //windows 不应该存储在项目路径中 会导致项目文件过大，运行缓慢
//        String path = "src/main/resources/static/images/userIcon/";
        //储存的路径对象
//        File fileDir = new File(path);
        //系统的存储文件名 (账号_icon.jpg / 账号_icon.png)
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append("_icon");
        builder.append(fileSuffix);
        String systemFileName = builder.toString();
        //文件对象
        //windows
//        File newFile = new File(fileDir.getAbsolutePath() + File.separator + systemFileName);
        //linux
        File newFile = new File("/opt/duyi/images/userIcon/"+systemFileName);

        //上传文件
        int result = 0;
        try {
            icon.transferTo(newFile);
            result = userMapper.insertUser(new User(name, SecureUtil.md5(password),managerState,systemFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        if(result == 1){
            TableHandler.refreshUser();
        }
        return result;
    }


    /**
     * 用户名验证
     * @param name 用户输入的账号
     * @return 该账号是否已经存在
     */
    @Override
    public Boolean verifyName(String name) {
        if(StrUtil.isEmpty(name)){
            return false;
        }
        //查询参数
        Map<String,Object> condition = new HashMap<>();
        condition.put("username",name);
        List<User> users = userMapper.findUserByCondition(condition);
        //没查到说明这个名字没有人使用，返回true
        if(ObjectUtil.isEmpty(users) || users.size() == 0){
            return true;
        }
        return false;
    }



}
