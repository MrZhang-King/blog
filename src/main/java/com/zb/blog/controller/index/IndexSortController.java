package com.zb.blog.controller.index;

import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Sort;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.SortService;
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

@Api(tags = "前台页面分类相关的接口")
@Controller
@RequestMapping("/blog/sort")
public class IndexSortController {

    //分类页面中的每页博客数量
    @Value("${indexSortBlogCount}")
    private Integer indexSortBlogCount;
    @Autowired
    private SortService sortService;
    @Autowired
    private BlogService blogService;

    @ApiOperation("根据id跳转分类页面")
    @ApiImplicitParam(name = "id", value = "分类id")
    @GetMapping("/toSort/{id}")
    public String toSort(@PathVariable("id") Integer id, Model model){
        Sort sort = sortService.getSortById(id);
        //查询第给定分类下的所有博客
        PageInfo<Blog> blogPageInfo = blogService.pageHelper(1,indexSortBlogCount, MapUtil.addElement("sort",sort.getId()));
        //存入request作用域中
        model.addAttribute("sortList",sortService.getUseSort());
        model.addAttribute("blogPageInfo",blogPageInfo);
        //前台样式使用
        model.addAttribute("style",sort.getId());
        model.addAttribute("indexNav",2);
        return "sort";
    }


    @ApiOperation("给分类下博客做分页")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "第几页"),
            @ApiImplicitParam(name = "sortId", value = "分类id")})
    @GetMapping("/doPage/{pageNum}/{sortId}")
    public String doPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("sortId") Integer sortId, Model model){
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,indexSortBlogCount,MapUtil.addElement("sort",sortId));
        model.addAttribute("blogPageInfo",pageInfo);
        return "sort::table_refresh";
    }
}
