package com.zb.blog.controller.index;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.zb.blog.dto.RequestResult;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Sort;
import com.zb.blog.entity.Tag;
import com.zb.blog.entity.User;
import com.zb.blog.service.BlogService;
import com.zb.blog.service.SortService;
import com.zb.blog.service.TagService;
import com.zb.blog.service.UserService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Api(tags = "前台页面导航栏相关的接口")
@Controller
@RequestMapping("/blog")
public class IndexNavController {

    //前台页面主页面中间栏每页的博客数量
    @Value("${indexBlogPageCount}")
    private Integer indexBlogPageCount;
    //前台页面主页面右边栏每页的博客数量
    @Value("${indexRightBlogPageCount}")
    private Integer indexRightBlogPageCount;
    //分类页面中的每页博客数量
    @Value("${indexSortBlogCount}")
    private Integer indexSortBlogCount;
    //标签页面中的每页博客数量
    @Value("${indexTagBlogCount}")
    private Integer indexTagBlogCount;

    @Autowired
    private BlogService blogService;
    @Autowired
    private SortService sortService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;

    /**
     * 跳转前台页面主页
     * @return 前台页面
     */
    @GetMapping("/index")
    @ApiOperation("首页接口")
    public String index( Model model){
        //获取第一页所有已发布的博客
        PageInfo<Blog> pageInfo = blogService.pageHelper(1,indexBlogPageCount, MapUtil.addElement("blogState",1));
        //获取按照分类下博客数量降序排列的第一页的所有正在使用中的分类
        PageInfo<Sort> sortPageInfo = sortService.orderSort("desc",1);
        //获取按照标签下博客数量降序排列的第一页的所有正在使用中的标签
        PageInfo<Tag> tagPageInfo = tagService.orderTag("desc",1);
        //查询按照发布时间降序排列的第一页的所有已发布的博客
        Map<String,Object> condition = new HashMap<>();
        condition.put("blogState",1);
        condition.put("descOrAsc","desc");
        PageInfo<Blog> blogPageInfo = blogService.pageHelper(1,indexRightBlogPageCount,condition);
        //查询到的所有数据放入request作用域中，用于页面展示
        model.addAttribute("pageInfo",pageInfo);
        model.addAttribute("sortPageInfo",sortPageInfo);
        model.addAttribute("tagPageInfo",tagPageInfo);
        model.addAttribute("blogPageInfo",blogPageInfo);
        model.addAttribute("indexNav",1);
        return "index";
    }

    /**
     * 跳转前台分类页面
     * 默认展示查询到的第一个分类下的所有博客
     * @return 分类页面
     */
    @ApiOperation("分类接口")
    @GetMapping("/toSort")
    public String toSort(Model model){
        //拿到所有的分类
        List<Sort> sortList = sortService.getUseSort();
        //查询第一个分类下的所有博客
        PageInfo<Blog> blogPageInfo = blogService.pageHelper(1,indexSortBlogCount,MapUtil.addElement("sort",sortList.get(0).getId()));
        //存入request作用域中
        model.addAttribute("sortList",sortList);
        model.addAttribute("blogPageInfo",blogPageInfo);
        //前台样式使用
        model.addAttribute("style",sortList.get(0).getId());
        model.addAttribute("indexNav",2);
        return "sort";
    }

    /**
     * 跳转前台标签页面
     * 默认展示查询到的第一个标签下的所有博客
     * @return 标签页面
     */
    @ApiOperation("标签接口")
    @GetMapping("/toTag")
    public String toTag(Model model){
        //拿到所有的分类
        List<Tag> tagList = tagService.getUseTag();
        //查询第一个标签下的所有博客
        PageInfo<Blog> blogPageInfo = blogService.pageHelper(1, indexTagBlogCount, MapUtil.addElement("tag",tagList.get(0).getId()));
        //存入request作用域中
        model.addAttribute("tagList",tagList);
        model.addAttribute("blogPageInfo",blogPageInfo);
        //前台样式使用
        model.addAttribute("style",tagList.get(0).getId());
        model.addAttribute("indexNav",3);
        return "tag";
    }

    /**
     * 跳转前台归档页面
     * 按照年份进行对博客分类
     * @return 归档页面
     */
    @ApiOperation("归档接口")
    @GetMapping("/toArchives")
    public String toArchives(Model model){
        //拿到所有博客
        List<Blog> blogList = blogService.selectByCondition(MapUtil.addElement("blogState",1));
        //用来存储发布时间的年份和年份下的所有博客的对应关系
        Map<Integer,List<Blog>> yearAndBlog = new HashMap<>();
        //遍历所有博客按照发布时间的年份进行分类
        for(Blog blog : blogList){
            //获取博客的发布时间
            Date publishTime = blog.getPublishTime();
            //获取发布时间的年份
            int year = DateUtil.year(publishTime);
            //根据年份获取年份对应的存储博客的list
            List<Blog> blogs = yearAndBlog.get(year);
            //如果没有取到，说明该年份对应的存储博客的list还未创建，需要创建一个
            if(ObjectUtil.isEmpty(blogs)){
                blogs = new ArrayList<>();
            }
            //将博客添加到年份对应的list集合中
            blogs.add(blog);
            //将年份和list添加到map中
            yearAndBlog.put(year,blogs);
        }
        //将博客总数和按照博客发布时间分好类的map集合存入request作用域中
        model.addAttribute("num",blogList.size());
        model.addAttribute("yearsAndBlog",yearAndBlog);
        model.addAttribute("indexNav",4);

        return "archives";
    }

    /**
     * 跳转前台关于我的页面
     * @return 关于我页面
     */
    @ApiOperation("关于我的接口")
    @GetMapping("/toAboutMe")
    public String toAboutMe(Model model) {
        model.addAttribute("indexNav",5);

        return "about";
    }

    /**
     * 搜索和搜索结果分页的接口
     * @param title 搜索的标题
     * @param pageNum 第几页
     * @param flag 标记  1：返回搜索页面  2：返回搜索页面中的片段
     *             为了区分是做分页的请求还是搜索的请求
     * @return 搜索页面/页面片段
     */
    @ApiOperation("搜索框/搜索内容分页的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "title", value = "搜索框中的标题"),
                        @ApiImplicitParam(name = "pageNum", value = "第几页的数据"),
                        @ApiImplicitParam(name = "flag", value = "1:返回搜索页面；2:返回搜索页面的片段(做分页用)")})
    @GetMapping("/search")
    public String search(String title, Integer pageNum, Integer flag, Model model) {
        PageInfo<Blog> pageInfo = blogService.pageHelper(pageNum,indexBlogPageCount,MapUtil.addElement("title",title));
        model.addAttribute("title",title);
        model.addAttribute("pageInfo",pageInfo);
        if(!ObjectUtil.isEmpty(flag) && flag == 1){
            return "search";
        }
        return "search::table_refresh";
    }


    //登录验证
    @ApiOperation("登录验证的接口")
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "用户名"),
            @ApiImplicitParam(name = "password", value = "密码")})
    @PostMapping("/login")
    @ResponseBody
    public RequestResult<User> login(String name , String password, Model model, HttpSession session) throws UnsupportedEncodingException {
        User user = userService.login(name,password);
        RequestResult<User> result = new RequestResult<>();
        //登录失败 跳转登录页 并存入错误信息
        if(ObjectUtil.isEmpty(user)){
            model.addAttribute("tip","用户名或密码有误");
//            return "/admin/login";
            result.setSuccess(false);
            result.setData(null);
            result.setMessage("用户名或密码有误");
            return result;
        }
        int state = user.getManagerState();
        if(state == 1){
            session.setAttribute("user",user);
            result.setSuccess(true);
            result.setData(user);
            result.setMessage("manager");
            return result;
        }
        //登录成功 将user对象和头像名存入session中
        session.setAttribute("user",user);
        result.setSuccess(true);
        result.setData(user);
        result.setMessage("登录成功");
        return result;
    }

}
