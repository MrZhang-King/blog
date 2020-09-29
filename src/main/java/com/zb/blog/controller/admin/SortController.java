package com.zb.blog.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Sort;
import com.zb.blog.service.SortService;
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

@Api(tags = "后台页面分类相关的接口")
@Controller
@RequestMapping("admin/sort")
public class SortController {

    @Autowired
    private SortService sortService;

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
        PageInfo<Sort> pageInfo = sortService.pageHelper(pageNum,condition);
        model.addAttribute("pageInfo",pageInfo);
        return "admin/sort::table_refresh";
    }

    /**
     * 删除分类的接口
     * @param id 要删除的分类id
     * @param pageNum 删除的分类所在的页码
     * @param condition 查询的条件
     * @return 替换片段
     */
    @ApiOperation("删除分类的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "删除的分类的id"),
            @ApiImplicitParam(name = "pageNum", value = "删除的分类所在的页码"),
            @ApiImplicitParam(name = "condition", value = "查询条件：title(分类名)")})
    @DeleteMapping
    public String delete(Integer id, Integer pageNum, @RequestParam Map<String,Object> condition, Model model){
        Integer result = sortService.delete(id);
        if(result.equals(0)){
            //删除失败,抛异常
        }
        //删除成功，查询删除数据所在页的所有博客
        PageInfo<Sort> pageInfo =sortService.pageHelper(pageNum,condition);
        model.addAttribute("pageInfo",pageInfo);
        //替换片段
        return "admin/sort::table_refresh";
    }


    //跳转分类添加页面
    @ApiOperation("跳转添加分类的页面")
    @GetMapping("/toAdd")
    public String toAdd(Model model){
        model.addAttribute("navIndex",2);
        return "admin/sort_add";
    }

    /**
     * 添加分类的接口
     * @param sort 分类
     * @return
     */
    @ApiOperation("添加分类的接口")
    @PostMapping("/add")
    public String add(Sort sort){
        //设置创建时间
        sort.setCreateTime(new Date());
        //设置修改时间
        sort.setUpdateTime(new Date());
        //设置分类状态
        sort.setSortState(0);
        //设置博客数量
        sort.setBlogCount(0);
        int serviceResult = sortService.add(sort);
        if(serviceResult == 0){
            //未成功
        }
        return "redirect:/admin/toSort/1";
    }

    /**
     * 跳转编辑页面
     * @param id 分类id
     * @param pageNum 分类所在的页码
     * @return 编辑页面
     */
    @ApiOperation("跳转分类编辑页面的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "要编辑的分类id"),
            @ApiImplicitParam(name = "pageNum", value = "要编辑的分类所在的页码")})
    @GetMapping("/toEdit/{id}/{pageNum}")
    public String toEdit(@PathVariable("id") Integer id, @PathVariable("pageNum") Integer pageNum, Model model){
        Sort sort = sortService.getSortById(id);
        if (ObjectUtil.isEmpty(sort)){
//            return "";
            //没有查到，跳转错误页
        }
        model.addAttribute("navIndex",2);
        model.addAttribute("sort",sort);
        model.addAttribute("pageNum",pageNum);
        return "admin/sort_edit";
    }

    @ApiOperation("编辑分类的接口")
    @ApiImplicitParam(name = "pageNum", value = "要编辑的分类所在的页码")
    @PostMapping("/edit")
    public String edit(Integer pageNum, Sort sort){
        sort.setSortState(0);
        sort.setUpdateTime(new Date());
        int result = sortService.edit(sort);
        return result == 1 ? "redirect:/admin/toSort/" + pageNum : "redirect:/admin/sort/toEdit/" + sort.getId() + "/" + pageNum;
    }
}
