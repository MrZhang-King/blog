package com.zb.blog.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Sort;
import com.zb.blog.entity.Tag;
import com.zb.blog.entity.User;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.SortService;
import com.zb.blog.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "后台页面博客相关的接口")
@Controller
@RequestMapping("/admin/blog")
public class BlogController {

    @Value("${adminBlogPageCount}")
    private Integer adminBlogPageCount;
    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;
    @Autowired
    private SortService sortService;

    /**
     * 添加一条博客的接口
     * @param blog
     * @return
     */
    @ApiOperation("添加博客的接口")
    @PostMapping("/blogAdd")
    public String blogAdd(Blog blog, MultipartFile blogImgFile, HttpSession session){
        //设置博客数量
        blog.setPageViewCount(0);
        //设置创建时间
        blog.setCreateTime(new Date());
        //设置修改时间
        blog.setUpdateTime(new Date());
        User user = (User) session.getAttribute("user");
        blog.setUserId(user.getId());
        if(blog.getBlogState() == 1){
            blog.setPublishTime(new Date());
        }else{
            blog.setBlogState(0);
        }
        int serviceResult = blogService.blogAdd(blog,blogImgFile);
        if(serviceResult == 0){
            //未成功
        }
        if(serviceResult == 2){
            //未成功 文件格式不正确
        }
        return "redirect:/admin/toManage/1";
    }

    //跳转博客添加页面,并查询所有的标签和分类
    @ApiOperation("跳转添加博客的页面")
    @GetMapping("/toBlog_add")
    public String blogAdd(Model model){
        //查询所有标签
        List<Tag> tagList = tagService.getUseTag();
        //查询所有分类
        List<Sort> sortList = sortService.getUseSort();
        model.addAttribute("navIndex",1);
        model.addAttribute("tagList",tagList);
        model.addAttribute("sortList",sortList);
        return "admin/blog_add";
    }
    /**
     * 做分页
     * @param pageNum 要哪一页的数据
     * @param condition 搜索时的参数
     * @return 页面替换
     */
    @ApiOperation("分页的接口(可支持按条件查询后在分页)")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "想要的页码"),
                        @ApiImplicitParam(name = "condition", value = "查询条件：title(标题) 和 sort(分类)")})
    @GetMapping("/doPage")
    public String pageHelper(Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,adminBlogPageCount,condition);
        model.addAttribute("pageInfo",pageInfo);
        return "admin/manage::table_refresh";
    }

    /**
     * 删除博客的接口
     * @param id 要删除的博客id
     * @param pageNum 删除的博客所在的页码
     * @param condition 查询的条件
     * @return 替换片段
     */
    @ApiOperation("删除博客的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除的博客的id"),
                        @ApiImplicitParam(name = "pageNum", value = "删除的博客所在的页码"),
                        @ApiImplicitParam(name = "condition", value = "查询条件：title(标题) 和 sort(分类)")})
    @DeleteMapping
    public String delete(Integer id, Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        Integer result = blogService.delete(id);
        if(result.equals(0)){
             //删除失败,抛异常
        }
        //删除成功，查询删除数据所在页的所有博客
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,adminBlogPageCount,condition);
        model.addAttribute("pageInfo",pageInfo);
        //替换片段
        return "admin/manage::table_refresh";
    }

    /**
     * 跳转编辑博客的页面
     * @param id 博客id
     * @param pageNum 博客所在的页码
     * @return 编辑页面
     */
    @ApiOperation("跳转编辑博客页面的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "要编辑的博客id"),
                        @ApiImplicitParam(name = "pageNum", value = "要编辑的博客所在的页码")})
    @GetMapping("/edit/{id}/{pageNum}")
    public String toEdit(@PathVariable("id") Integer id, @PathVariable("pageNum") Integer pageNum, Model model){
        Blog blog = blogService.getBlogById(id);
        if (ObjectUtil.isEmpty(blog)){
//            return "";
            //没有查到，跳转错误页
        }
        //查询所有标签
        List<Tag> tagList = tagService.getUseTag();
        //查询所有分类
        List<Sort> sortList = sortService.getUseSort();
        model.addAttribute("navIndex",1);
        model.addAttribute("tagList",tagList);
        model.addAttribute("sortList",sortList);
        model.addAttribute("blog",blog);
        model.addAttribute("pageNum",pageNum);
        return "admin/blog_edit";
    }

    @ApiOperation("编辑博客的接口")
    @ApiImplicitParam(name = "pageNum", value = "要编辑的博客所在的页码")
    @PostMapping("/blogEdit")
    public String edit(Integer pageNum, Blog blog){
        int result = blogService.edit(blog);
        return result == 1 ? "redirect:/admin/toManage/" + pageNum : "redirect:/admin/blog/edit/" + blog.getId();
    }
}
