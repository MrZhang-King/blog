package com.zb.blog.controller.index;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Tag;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.TagService;
import com.zb.blog.utils.MapUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Api(tags = "前台页面标签相关的接口")
@Controller
@RequestMapping("/blog/tag")
public class IndexTagController {

    //分类页面中的每页博客数量
    @Value("${indexTagBlogCount}")
    private Integer indexTagBlogCount;
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogService blogService;

    @ApiOperation("根据id跳转标签页面")
    @ApiImplicitParam(name = "id", value = "标签id")
    @GetMapping("/toTag/{id}")
    public String toTag(@PathVariable("id") Integer id, Model model){
        Tag tag = tagService.getTagById(id);
        //查询第给定分类下的所有博客
        PageInfo<Blog> blogPageInfo = blogService.pageHelper(1,indexTagBlogCount, MapUtil.addElement("tag",tag.getId()));
        //存入request作用域中
        model.addAttribute("tagList",tagService.getUseTag());
        model.addAttribute("blogPageInfo",blogPageInfo);
        //前台样式使用
        model.addAttribute("style",tag.getId());
        model.addAttribute("indexNav",3);
        return "tag";
    }

    @ApiOperation("给标签下博客做分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "第几页"),
                        @ApiImplicitParam(name = "tagId", value = "标签id")})
    @GetMapping("/doPage/{pageNum}/{tagId}")
    public String doPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("tagId") String tagId, Model model){
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,indexTagBlogCount,MapUtil.addElement("tag",tagId));
        model.addAttribute("blogPageInfo",pageInfo);
        return "tag::table_refresh";
    }
}
