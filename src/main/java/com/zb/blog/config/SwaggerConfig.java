package com.zb.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * 使用Swagger需要创建一个摘要 ---> Docket
     * 摘要参数如下：
     *      文档类型：使用Swagger2  --->  DocumentationType.SWAGGER_2
     *      文档通过一系列的选择器组成  ---> api  path
     *      select() 的华润系spi()和paths()
     *      apis 查找生成哪些controller的接口
     *      path 在查找出来的接口中进行筛选
     * @return
     */
    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2).select()
//                .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.zb.blog.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                ;
    }

    /**
     * 自定义文档的介绍
     * 通过ApiInfoBuilder创建ApiInfo
     * 参数可以设置  title description version 标题  描述  版本等等
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("个人博客的小练习")
                .description("基于SpringBoot实现的一个个人博客项目")
                .version("1.0")
                .build();
    }
}
