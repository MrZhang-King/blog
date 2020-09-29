package com.zb.blog.service;

import com.zb.blog.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User login(String name, String password);

    Integer register(MultipartFile icon, Integer managerState, String name, String password);

    Boolean verifyName(String name);
}
