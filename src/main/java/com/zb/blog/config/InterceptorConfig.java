package com.zb.blog.config;

import com.zb.blog.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private UserInterceptor userInterceptor;

    //进入管理页面必须登录的拦截器配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor).addPathPatterns("/admin/**");
    }

    //不需要重启项目更新userIcon中的图片
    //场景：当用户注册后进行登录时，上传的头像并没有加载到target目录中，使用此配置解决该问题
    //addResourceHandler("/img/**")配置表示释放当前img文件夹下的所有资源（这里我是吧图片放到idea的img目录下）
//addResourceLocations配置你图片在系统中的保存路径：格式是file:路径
//*注意*路径前一定加file：

    /**
     * 不需要重启项目更新userIcon中的图片
     * 场景：当用户注册后进行登录时，上传的头像并没有加载到target目录中，使用此配置解决该问题
     *    addResourceHandler("/img/**")配置表示释放当前userIcon文件夹下的所有资源（这里我是把图片放到idea的userIcon目录下）
     *    addResourceLocations配置你图片在系统中的保存路径：格式是file:路径
     * 注意:路径前一定加file：
     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
////        String path = "src/main/resources/static/images/";
////        File file = new File(path);
////        registry.addResourceHandler("/images/**")
////                .addResourceLocations("file:./");
//        registry.addResourceHandler("/images/**").
//                addResourceLocations("file:///opt/duyi/");
////        registry.addResourceHandler("/files/**")
////                .addResourceLocations("file:///var/spring/uploaded_files");
////                .addResourceLocations("file:D:\\Users\\Lenovo\\blog\\src\\main\\resources\\static\\images//");
//    }
}



