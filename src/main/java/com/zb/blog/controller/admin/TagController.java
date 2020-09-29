package com.zb.blog.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Tag;
import com.zb.blog.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Api(tags = "后台页面标签相关的接口")
@Controller
@RequestMapping("/admin/tag")
public class TagController {

    @Autowired
    private TagService tagService;


    /**
     * 做分页
     * @param pageNum 要哪一页的数据
     * @param condition 搜索时的参数
     * @return 页面替换
     */
    @ApiOperation("分页的接口(可支持按条件查询后在分页)")
    @ApiImplicitParams({@ApiImplicitParam(name = "pageNum", value = "想要的页码"),
            @ApiImplicitParam(name = "condition", value = "查询条件：tagName(标签名)")})
    @GetMapping("/doPage")
    public String pageHelper(Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        PageInfo<Tag> pageInfo = tagService.pageHelper(pageNum,condition);
        model.addAttribute("pageInfo",pageInfo);
        return "admin/tag::table_refresh";
    }

    //跳转分类添加页面
    @ApiOperation("跳转添加标签的页面")
    @GetMapping("/toAdd")
    public String toAdd(Model model){
        model.addAttribute("navIndex",3);
        return "admin/tag_add";
    }

    /**
     * 添加标签的接口
     * @param tag 标签对象
     * @return
     */
    @ApiOperation("添加标签的接口")
    @PostMapping("/add")
    public String add(Tag tag){
        //设置创建时间
        tag.setCreateTime(new Date());
        //设置修改时间
        tag.setUpdateTime(new Date());
        //设置标签状态
        tag.setTagState(0);
        //设置博客数量
        tag.setBlogCount(0);
        int serviceResult = tagService.add(tag);
        if(serviceResult == 0){
            //未成功
        }
        return "redirect:/admin/toTag/1";
    }

    @ApiOperation("删除标签的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除的标签的id"),
            @ApiImplicitParam(name = "pageNum", value = "删除的标签所在的页码"),
            @ApiImplicitParam(name = "condition", value = "查询条件：title(标签名)")})
    @DeleteMapping
    public String delete(Integer id, Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        Integer result = tagService.delete(id);
        if(result.equals(0)){
            //删除失败,抛异常
        }
        //删除成功，查询删除数据所在页的所有博客
        PageInfo<Tag> pageInfo =tagService.pageHelper(pageNum,condition);
        model.addAttribute("pageInfo",pageInfo);
        //替换片段
        return "admin/tag::table_refresh";
    }


    /**
     * 跳转编辑页面
     * @param id 标签id
     * @param pageNum 标签所在的页码
     * @return 编辑页面
     */
    @ApiOperation("跳转标签编辑页面的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "要编辑的标签id"),
            @ApiImplicitParam(name = "pageNum", value = "要编辑的标签所在的页码")})
    @GetMapping("/toEdit/{id}/{pageNum}")
    public String toEdit(@PathVariable("id") Integer id, @PathVariable("pageNum") Integer pageNum, Model model){
        Tag tag = tagService.getTagById(id);
        if (ObjectUtil.isEmpty(tag)){
//            return "";
            //没有查到，跳转错误页
        }
        model.addAttribute("navIndex",3);
        model.addAttribute("tag",tag);
        model.addAttribute("pageNum",pageNum);
        return "admin/tag_edit";
    }

    @ApiOperation("编辑标签的接口")
    @ApiImplicitParam(name = "pageNum", value = "要编辑的标签所在的页码")
    @PutMapping
    public String edit(Integer pageNum, Tag tag){
        tag.setTagState(0);
        tag.setUpdateTime(new Date());
        int result = tagService.edit(tag);
        return result == 1 ? "redirect:/admin/toTag/" + pageNum : "redirect:/admin/tag/toEdit/" + tag.getId() + "/" + pageNum;
    }

}
