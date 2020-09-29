package com.zb.blog.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Sort;
import com.zb.blog.entity.Tag;
import com.zb.blog.entity.User;
import com.zb.blog.handler.TableHandler;
import com.zb.blog.mapper.BlogMapper;
import com.zb.blog.mapper.SortMapper;
import com.zb.blog.mapper.TagMapper;
import com.zb.blog.service.BlogService;
import com.zb.blog.utils.ListUtil;
import com.zb.blog.utils.MarkdownUtil;
import com.zb.blog.utils.Verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private SortMapper sortMapper;
    @Autowired
    private Verify verify;
//    @Autowired
//    private SortAndTagHandler sortAndTagHandler;

    @Override
    public List<Blog> selectAll() {
        return blogMapper.findBlogAll();
    }

    /**
     * 添加博客
     * @param blog 博客对象
     * @param blogImgFile 博客首图的文件对象
     * @return 结果 1：成功   0；失败  2：文件格式有误
     */
    @Override
    @Transactional
    public int blogAdd(Blog blog, MultipartFile blogImgFile) {
        if(ObjectUtil.isEmpty(blogImgFile)){
            return 0;
        }
        //获取文件名
        String fileName = blogImgFile.getOriginalFilename();
        //验证文件格式
        if(!verify.verifyIcon(fileName)){
            return 2;
        }
        //文件后缀
        String fileSuffix = fileName.substring(fileName.indexOf("."));
        //存储路径
//        static/userIcon/张斌1_icon.jpg
        //windows
//        String path = "src/main/resources/static/images/blogImg/";
//        //储存的路径对象
//        File fileDir = new File(path);
        //系统的存储文件名 (随机id_img.jpg / 随机id_img.png)
        StringBuilder builder = new StringBuilder();
        builder.append(IdUtil.simpleUUID());
        builder.append("_img");
        builder.append(fileSuffix);
        String systemFileName = builder.toString();
        //文件对象
//        File newFile = new File(fileDir.getAbsolutePath() + File.separator + systemFileName);
        //上传文件
        //linux
        File newFile = new File("/opt/duyi/images/blogImg/"+systemFileName);
//        int result = 0;
        try {
            blogImgFile.transferTo(newFile);
//            result = userMapper.insertUser(new User(name, SecureUtil.md5(password),managerState,systemFileName));
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        boolean result = updateCount(blog,true);
        if(!result){
            return 0;
        }
        blog.setBlogImg(systemFileName);
        return blogMapper.insertBlog(blog);
    }

    /**
     * 做分页的方法
     * @param pageNum 第几页
     * @param pageCount 每页显示几条
     * @param condition 查询条件
     * @return 查询结果
     */
    @Override
    public PageInfo<Blog> pageHelper(Integer pageNum, Integer pageCount, Map<String, Object> condition) {
        //第几页  每页显示几条数据
        PageHelper.startPage(pageNum,pageCount);
        List<Blog> blogList;
        //没有条件的时候查询所有
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            blogList = blogMapper.findBlogAll();
        }else{//按照条件查
            blogList = blogMapper.findBlogByCondition(condition);
        }
        //将blog中的分类对象进行赋值,方便前端显示
//        System.out.println("查询到的map:"+sortMap);
        //遍历所有的博客对象，修改属性sortObj和tagObj的值（原来是分类的id，修改为分类名，方便前端展示）
        for (Blog blog : blogList){
            //获取分类和标签的id
            Integer sortId;
            String tagIdStr;
            try{
                sortId = Integer.parseInt(blog.getSort());
                tagIdStr = blog.getTag();
            } catch (NumberFormatException e){
                return null;
            }
            //处理分类
            Sort sort = TableHandler.getSortMap().get(sortId);
//            System.out.println("拿到的："+sort);
            blog.setSortObj(sort);
            //处理标签
            String[] tagIdArr = tagIdStr.split(",");
            if(!ObjectUtil.isEmpty(tagIdArr) || tagIdArr.length != 0){
                List<Tag> tagList = new ArrayList<>();
                for(String tagId : tagIdArr) {
                    Tag tag = TableHandler.getTagMap().get(Integer.parseInt(tagId));
                    tagList.add(tag);
                }
                blog.setTagObj(tagList);
            }
            //处理user对象
            int userId = blog.getUserId();
            User user = TableHandler.getUserMap().get(userId);
            blog.setUserObj(user);

        }
        return new PageInfo<>(blogList);
    }

    /**
     * 删除博客的方法（真实操作是将博客的状态改为-1）
     * @param id 博客的id
     * @return 0：删除失败 1：删除成功
     */
    @Override
    @Transactional
    public int delete(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return 0;
        }
        //根据id查询该博客
        List<Blog> blogList = blogMapper.findBlogByIds(ListUtil.addElement(id));
        //如果没有查到，返回失败
        if(ObjectUtil.isEmpty(blogList) || blogList.size() == 0){
            return 0;
        }
        Blog blog = blogList.get(0);
        //修改博客标签和分类下的博客数量
        boolean result = updateCount(blog,false);
        if(!result){
            return 0;
        }
        //设置博客的状态和更改时间
        blog.setBlogState(-1);
        blog.setUpdateTime(new Date());
        //修改数据库中该条博客
        return blogMapper.updateBlog(blog);
    }

    /**
     * 根据博客id查询博客
     * @param id 博客id
     * @return 查询到的博客  null:没有查到
     */
    @Override
    public Blog getBlogById(Integer id) {
        List<Blog> blogList =  blogMapper.findBlogByIds(ListUtil.addElement(id));
        if(ObjectUtil.isEmpty(blogList) || blogList.size() == 0){
            return null;
        }
        return blogList.get(0);
    }

    /**
     * 编辑博客（修改博客）
     * @param blog 修改过的博客
     * @return 1：成功 0：失败
     */
    @Override
    @Transactional
    public Integer edit(Blog blog) {
        //博客是否可以点赞的状态如果不是1，就是0（前端有课能给的数据时null）
        blog.setLikeState(blog.getLikeState() == null ? 0 : 1);
        //以下分别是是否可以转载，是否可以推荐，是否可以评论状态的修改
        blog.setReprintState(blog.getReprintState() == null ? 0 : 1);
        blog.setRecommendState(blog.getRecommendState() == null ? 0 : 1);
        blog.setCommentState(ObjectUtil.isEmpty(blog.getCommentState()) ? 0 : 1);
        if(ObjectUtil.isEmpty(blog)){
            return 0;
        }
        //查找旧的blog(博客id不能进行修改，所以可以通过修改后的博客id查找)
        Blog oldBlog = getBlogById(blog.getId());
        //修改旧博客分类和标签下的博客的数量并判读修改结果
        if(!updateCount(oldBlog,false)) {
            return 0;
        }
        //修改新博客分类和标签下的博客的数量并判读修改结果
        if(!updateCount(blog,true)){
            return 0;
        }
        blog.setUpdateTime(new Date());
        return blogMapper.updateBlog(blog);
    }




    //=======================================================

    /**
     * 根据给定的博客查询对应标签和分类，并修改标签和分类下博客的数量
     * @param blog 给定的博客
     * @param addOrDown 标签和分类下的博客数量加一或减一  false：-1   true：+1
     * @return 修改是否成功 若给的blog中的标签和分类为空，返回true，因为此时该博客没有选择分类，不存在修改
     */
    @Transactional
    boolean updateCount(Blog blog, boolean addOrDown){
        if(ObjectUtil.isEmpty(blog)){
            return false;
        }
        //博客的状态为1表示是已发布，只有已发布的博客标签和分类才会记录数量
        if(blog.getBlogState() != 1){
            return true;
        }
        String tagIds = blog.getTag();
        String sortIdStr = blog.getSort();
        if(StrUtil.isEmpty(tagIds) && ObjectUtil.isEmpty(sortIdStr)){
            return true;
        }
        //处理标签
        String[] tagArray = tagIds.split(",");
        for(int i = 0; i < tagArray.length; i++){
            String tagIdStr = tagArray[i];
            if(StrUtil.isEmpty(tagIdStr)){
                continue;
            }
            Integer tagId;
            try {
                tagId = Integer.parseInt(tagIdStr);
            } catch (NumberFormatException e){
                return false;
            }
            //根据标签名查找对应的标签
            List<Tag> tagList = tagMapper.findTagByIds(ListUtil.addElement(tagId));
            if(ObjectUtil.isEmpty(tagList) || tagList.size() == 0){
                return false;
            }
            //拿到标签
            Tag tag = tagList.get(0);
            if(ObjectUtil.isEmpty(tag)){
                return false;
            }
            //修改标签下的博客数量
            tag.setBlogCount(addOrDown ? tag.getBlogCount() + 1 : tag.getBlogCount() - 1);
            if(tagMapper.updateTag(tag) != 1){
                return false;
            }
        }
        //处理分类
        //根据分类名查找对应的分类
        Integer sortId;
        try{
            sortId = Integer.parseInt(sortIdStr);
        } catch(NumberFormatException e) {
            return false;
        }
        List<Sort> sortList = sortMapper.findSortByIds(ListUtil.addElement(sortId));
        //没有查到说明不存在，博客数量修改失败
        if(ObjectUtil.isEmpty(sortList) || sortList.size() == 0){
            return false;
        }
        Sort sort = sortList.get(0);
        if(ObjectUtil.isEmpty(sort)){
            return false;
        }
        sort.setBlogCount(addOrDown ? sort.getBlogCount() + 1 : sort.getBlogCount() - 1);
        return sortMapper.updateSort(sort) == 1;
    }


    //==============================================


    /**
     * 给博客对象中的sortObj 和 tagObj赋值
     * 在缓存中通过id查询到对应的对象，赋值给blog中的属性
     * @param blog 博客
     * @return 修改后的blog对象
     */
    @Override
    public Blog setPropertyObj(Blog blog) {
        //将博客的内容从Markdown形式转为HTML形式
        String content = MarkdownUtil.markdownToHtmlExtens(blog.getContent());
        blog.setContent(content);
        //给博客中的sortObj赋值(从分类的缓存map中通过id获取到sort对象)
        blog.setSortObj(TableHandler.getSortMap().get(Integer.parseInt(blog.getSort())));
        //给博客中的tagObj赋值(从标签的缓存map中通过id获取到tag对象)
        String[] tagIds = blog.getTag().split(",");
        List<Tag> tagList = new ArrayList<>();
        for (String tagId : tagIds){
            Tag tag = TableHandler.getTagMap().get(Integer.parseInt(tagId));
            tagList.add(tag);
        }
        blog.setTagObj(tagList);
        blog.setUserObj(TableHandler.getUserMap().get(blog.getUserId()));
        return blog;
    }

    /**
     * 根据跟定的条件查询博客
     * @param condition 给定的条件
     * @return 查询的结果
     */
    @Override
    public List<Blog> selectByCondition(Map<String, Object> condition) {
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            return null;
        }
        return blogMapper.findBlogByCondition(condition);
    }

    /**
     * 增加博客的浏览人数，每次增加 1
     * @param blog 要增加浏览人数的博客
     * @return 0：增加失败  1：增加成功
     */
    @Override
    public int addBlogViewCount(Blog blog) {
        blog.setPageViewCount(blog.getPageViewCount() + 1);
        return blogMapper.updateViewCount(blog);
    }

}
