package com.zb.blog.controller.index;

import cn.hutool.core.util.ObjectUtil;
import com.zb.blog.dto.RequestResult;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Comment;
import com.zb.blog.entity.User;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "")
@RequestMapping("blog/comment")
@Controller
public class IndexCommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;

    @PostMapping("/add")
    @ResponseBody
    public RequestResult<String> add(Comment comment, Integer userId, Model model, HttpSession session){
        RequestResult<String> result = new RequestResult<>();
        User user = (User) session.getAttribute("user");
        if(ObjectUtil.isEmpty(user)){
            result.setSuccess(false);
            result.setData("noLogin");
            result.setMessage("noLogin");
            return result;
        }
        comment.setCommentPersonName(user.getUsername());
        comment.setCommentPersonId(user.getId());
        comment.setCreateTime(new Date());
        if(user.getId().equals(userId)){
            comment.setBloggerState(1);
        }else{
            comment.setBloggerState(0);
        }
        int serviceResult = commentService.addComment(comment);
        //成功
        if(serviceResult == 1) {
            result.setSuccess(true);
            result.setData("");
            result.setMessage("ok");
            return result;
        }
        //失败
        result.setSuccess(false);
        result.setData("The content contains sensitive fields");
        result.setMessage("The content contains sensitive fields");
        return result;

    }


    /**
     *刷新评论的接口
     * @param id 博客id
     * @return 博客详情页的评论局部页面
     */
    @ApiOperation("刷新评论的接口")
    @ApiImplicitParam(name = "id", value = "博客id")
    @PostMapping("/flushComment")
    public String toBlog(Integer id, Model model){
        //查询该博客的所有评论
        Map<Comment, List<Comment>> commentMap = commentService.getCommentsByBlog (id);
        Blog blog = blogService.getBlogById(id);
        model.addAttribute("commentMap",commentMap);
        model.addAttribute("blog",blog);

        return "blog::refresh_area";
    }

}
