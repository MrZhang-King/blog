package com.zb.blog.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.zb.blog.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserInterceptor implements HandlerInterceptor {

    /**
     * 控制未登录的用户进行操作
     * @return 如果未登录，跳转到登录页
     */
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
       User user = (User) request.getSession().getAttribute("user");
       if(ObjectUtil.isEmpty(user) || user.getManagerState() == 0){
           response.sendRedirect("/blog/index");
           return false;
       }
        return true;
    }
}
