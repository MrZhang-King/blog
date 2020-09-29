package com.zb.blog.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Comment;
import com.zb.blog.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "后台页面评论相关的接口")
@Controller
@RequestMapping("admin/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 做分页
     * @param pageNum 要哪一页的数据
     * @param condition 搜索时的博客标题 评论状态
     * @return 页面替换
     */
    @ApiOperation("分页的接口(可支持按条件查询后再分页)")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "想要的页码"),
            @ApiImplicitParam(name = "condition", value = "查询条件：blogTitle(博客的标题) commentState(评论状态)")})
    @GetMapping("/doPage")
    public String pageHelper(Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        PageInfo<Comment> pageInfo = commentService.pageHelper(pageNum,condition);
        model.addAttribute("pageInfo",pageInfo);
        return "admin/comment::table_refresh";
    }

    /**
     * 删除评论的接口
     * @param id 要删除的评论id
     * @param pageNum 删除的评论所在的页码
     * @param condition 查询的条件 1.博客标题  2.评论状态
     * @return 替换片段
     */
    @ApiOperation("删除评论的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除的评论的id"),
            @ApiImplicitParam(name = "pageNum", value = "删除的评论所在的页码"),
            @ApiImplicitParam(name = "condition", value = "查询条件：title(博客标题)  commentState(评论状态)")})
    @DeleteMapping
    public String delete(Integer id, Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        Integer result = commentService.delete(id);
        if(result.equals(0)){
            //删除失败,抛异常
        }
        //删除成功，查询删除数据所在页的所有评论
        PageInfo<Comment> pageInfo =commentService.pageHelper(pageNum,condition);
        model.addAttribute("pageInfo",pageInfo);
        //替换片段
        return "admin/comment::table_refresh";
    }



    /**
     * 跳转详情页面
     * @param id 评论id
     * @return 详情页面
     */
    @ApiOperation("跳转评论详情页面的接口")
    @ApiImplicitParam(name = "id", value = "要编辑的分类id")
    @GetMapping("/toComment-detail/{id}/{pageNum}/{blogTitle}/{commentState}")
    public String toEdit(@PathVariable("id") Integer id, @PathVariable("pageNum") Integer pageNum,
                         @PathVariable("blogTitle") String blogTitle, @PathVariable("commentState") String commentState, Model model){
        Comment comment = commentService.getCommentById(id);
        if (ObjectUtil.isEmpty(comment)){
//            return "";
            //没有查到，跳转错误页
        }
        model.addAttribute("blogTitle",blogTitle);
        model.addAttribute("commentState",commentState);
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navIndex",4);
        model.addAttribute("comment",comment);
        return "admin/comment_detail";
    }


    /**
     * 删除评论的接口
     * @param id 要删除的评论id
     * @return 替换片段
     */
    @ApiOperation("删除评论的接口")
    @ApiImplicitParam(name = "id", value = "删除的评论的id")
    @PostMapping("/delete")
//    @DeleteMapping
    public String delete(Integer id, Model model){
        Integer result = commentService.delete(id);
        if(result.equals(0)){
            //删除失败,抛异常
        }
        //删除成功，查询已删除的评论
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment",comment);
        //替换片段
        return "admin/comment_detail::table_refresh";
    }




    /**
     * 重新审核和取消删除可以是同一个接口，因为都是进行了重新审核，这样写为了提高可读性（完全可以供用一个接口）
     * 取消删除的接口
     * @param id 要取消删除的评论id
     * @return 替换片段
     */
    @ApiOperation("取消删除的接口")
    @ApiImplicitParam(name = "id", value = "要取消删除的评论id")
    @PostMapping("/rollBackDelete")
    public String rollBackDelete(Integer id, Model model){
        Integer result = commentService.auditCommentAndAlterState(id);
        if(result.equals(0)){
            //取消删除失败,抛异常
        }
        //取消删除成功，查询取消删除的评论
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment",comment);
        //替换片段
        return "admin/comment_detail::table_refresh";
    }
    /**
     * 重新审核的接口
     * @param id 要重新审核的评论id
     * @return 替换片段
     */
    @ApiOperation("重新审核的接口")
    @ApiImplicitParam(name = "id", value = "要重新审核的评论id")
    @PostMapping("/againAudit")
    public String againAudit(Integer id, Model model){
        Integer result = commentService.auditCommentAndAlterState(id);
        if(result.equals(0)){
            //重新审核失败,抛异常
        }
        //重新审核成功，查询重新审核的评论
        Comment comment = commentService.getCommentById(id);
        model.addAttribute("comment",comment);
        //替换片段
        return "admin/comment_detail::table_refresh";
    }

}
