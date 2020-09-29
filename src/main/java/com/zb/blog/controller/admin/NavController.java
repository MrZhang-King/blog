package com.zb.blog.controller.admin;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Comment;
import com.zb.blog.entity.Sort;
import com.zb.blog.entity.Tag;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.CommentService;
import com.zb.blog.service.SortService;
import com.zb.blog.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台管理页面导航的相关接口(使用该接口用户必须是登录状态)")
@Controller
@RequestMapping("/admin")
public class NavController {
    @Value("${adminBlogPageCount}")
    private Integer adminBlogPageCount;
    @Autowired
    private SortService sortService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;

    //跳转博客管理页面
    @ApiOperation("跳转博客管理页面")
    @GetMapping("/toManage/{pageNum}")
    public String toManage(@PathVariable("pageNum") Integer pageNum, Model model){
        //查询所有未删除的分类
        List<Sort> sortList = sortService.getUseSort();
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,adminBlogPageCount,null);
        model.addAttribute("sortList",sortList);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("navIndex",1);
        return "admin/manage";
    }


    @ApiOperation("跳转分类管理页面")
    @GetMapping("/toSort/{pageNum}")
    public String toSort(@PathVariable("pageNum") Integer pageNum, Model model){
        PageInfo<Sort> pageInfo = sortService.pageHelper(pageNum, null);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("navIndex",2);
        return "admin/sort";
    }

    @ApiOperation("跳转标签管理页面")
    @GetMapping("/toTag/{pageNum}")
    public String toTag(@PathVariable("pageNum") Integer pageNum, Model model){
        PageInfo<Tag> pageInfo = tagService.pageHelper(pageNum, null);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("navIndex",3);
        return "admin/tag";
    }

    @ApiOperation("跳转评论管理页面")
    @ApiImplicitParam(name = "pageNum", value = "第几页")
    @GetMapping("/toComment/{pageNum}")
    public String toComment(@PathVariable("pageNum") Integer pageNum, Model model){
        PageInfo<Comment> pageInfo = commentService.pageHelper(pageNum, null);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navIndex",4);
        return "admin/comment";
    }

    @ApiOperation("跳转评论管理页面(列表按钮使用)")
    @GetMapping("/toComment/{pageNum}/{blogTitle}/{commentState}")
    public String toCommentForTab(@PathVariable("pageNum") Integer pageNum,
                                  @PathVariable("blogTitle") String blogTitle,
                                  @PathVariable("commentState") Integer commentState, Model model){
        Map<String,Object> condition = new HashMap<>();
        if("@--!!@@".equals(blogTitle)){
            blogTitle = "";
        }else{
            condition.put("blogTitle",blogTitle);
        }

        if(commentState != 2){
            condition.put("commentState",commentState);
        }
        PageInfo<Comment> pageInfo = commentService.pageHelper(pageNum, condition);
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("commentState",commentState);
        model.addAttribute("blogTitle",blogTitle);
        model.addAttribute("navIndex",4);
        return "admin/comment";
    }


}
