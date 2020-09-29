package com.zb.blog.controller.index;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Comment;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.CommentService;
import com.zb.blog.utils.MapUtil;
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

import java.util.List;
import java.util.Map;

@Api(tags = "前台页面博客相关的接口")
@Controller
@RequestMapping("/blog/blog")
public class IndexBlogController {

    //前台页面主页面中间栏每页的博客数量
    @Value("${indexBlogPageCount}")
    private Integer indexBlogPageCount;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;

    /**
     *跳转单条博客详细页面
     * @param id 博客id
     * @return 博客详细页面
     */
    @ApiOperation("跳转单条博客详细页面")
    @ApiImplicitParam(name = "id", value = "博客id")
    @GetMapping("/toBlog/{id}")
    public String toBlog(@PathVariable("id") Integer id, Model model){
        //通过id查询博客对象
        Blog blog = blogService.getBlogById(id);
        //增加该博客的浏览人数
        blogService.addBlogViewCount(blog);
        //查询该博客的所有评论
        Map<Comment, List<Comment>> commentMap = commentService.getCommentsByBlog (blog);
        //给博客对象中的sortObj 和 tagObj  userOBj赋值
        blog = blogService.setPropertyObj(blog);
        model.addAttribute("blog",blog);
        model.addAttribute("commentMap",commentMap);
        return "blog";
    }

    /**
     * 做分页的接口
     * @param pageNum 第几页
     * @return 替换的片段
     */
    @ApiOperation("博客分页的接口")
   @ApiImplicitParam(name = "pageNum", value = "想要的页码")
    @GetMapping("/doPage")
    public String doPage(Integer pageNum, Model model){
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,indexBlogPageCount, MapUtil.addElement("blogState",1));
        model.addAttribute("pageInfo",pageInfo);
        return "index::table_refresh";
    }
}
