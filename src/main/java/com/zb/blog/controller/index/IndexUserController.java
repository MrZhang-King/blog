package com.zb.blog.controller.index;

import com.zb.blog.dto.RequestResult;
import com.zb.blog.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

@Api(tags = "用户相关的接口")
@Controller
@RequestMapping("/blog")
public class IndexUserController {

    @Autowired
    private UserService userService;


    //注销（退出登录）
    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/blog/index";
    }


    //跳转注册页
    @ApiIgnore
    @GetMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    //注册验证
    @ApiOperation("注册验证的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "用户名"),
            @ApiImplicitParam(name = "password", value = "密码"),
            @ApiImplicitParam(name = "icon", value = "头像")})
    @PostMapping("/register")
    public String register(String name, String password, MultipartFile icon, Model model){
        //result 0：用户名或密码不能为空  1：成功  2：头像文件格式不正确（jpg/png）
        int serviceResult = userService.register(icon, 0, name, password);
        if(serviceResult == 0){
           model.addAttribute("message","用户名或密码不能为空");
            return "register";
        }
        if(serviceResult == 1){
            model.addAttribute("message","注册成功");
            return "register";
        }
        model.addAttribute("message","头像文件格式不正确（jpg/png）");
        return "register";
    }

    //验证注册时用户输入的账号是否已经存在
    @ApiOperation("验证注册时用户输入的账号是否已经存在的接口")
    @ApiImplicitParam(name = "name", value = "用户名")
    @PostMapping("/verifyName/{name}")
    @ResponseBody
    public RequestResult<Boolean> verifyName(@PathVariable("name") String name){
        Boolean b = userService.verifyName(name);
        RequestResult<Boolean> result = new RequestResult<>();
        //不存在 可以使用的账号
        if(b){
            result.setSuccess(true);
            result.setData(true);
            result.setMessage("ok");
            return result;
        }
        //已存在的账号 验证不通过
        result.setSuccess(false);
        result.setData(false);
        result.setMessage("用户名已存在");
        return result;
    }


}
