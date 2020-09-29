package com.zb.blog.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zb.blog.entity.Blog;
import com.zb.blog.entity.Comment;
import com.zb.blog.entity.User;
import com.zb.blog.handler.TableHandler;
import com.zb.blog.mapper.BlogMapper;
import com.zb.blog.mapper.CommentMapper;
import com.zb.blog.service.CommentService;
import com.zb.blog.utils.KeyWordFilter;
import com.zb.blog.utils.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    @Value("${commentPageCount}")
    private Integer pageCount;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private KeyWordFilter keyWordFilter;

    /**
     * 做分页的方法
     * @param pageNum 第几页
     * @param condition 查询条件
     *                  参数condition中包含了pageNum 这样写是为了可读性好
     * @return 查询结果
     */
    @Override
    public PageInfo<Comment> pageHelper(Integer pageNum, Map<String, Object> condition) {
        if(ObjectUtil.isEmpty(pageNum)){
            return null;
        }
        //第几页  每页显示几条数据
        PageHelper.startPage(pageNum,pageCount);
        List<Comment> commentList;
        //没有条件的时候查询所有
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            commentList = commentMapper.findCommentAll();
        }else{//按照条件查
            commentList = commentMapper.findCommentByCondition(condition);
        }
        return new PageInfo<>(commentList);
    }


    /**
     * 删除评论的方法（真实操作是将评论的状态改为-1）
     * @param id 评论的id
     * @return 0：删除失败 1：删除成功
     */
    @Override
    public int delete(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return 0;
        }
        //根据id查询该评论
        List<Comment> commentList = commentMapper.findCommentByIds(ListUtil.addElement(id));
        //如果没有查到，返回失败
        if(ObjectUtil.isEmpty(commentList) || commentList.size() == 0){
            return 0;
        }
        Comment comment = commentList.get(0);
        //设置评论的状态和更改时间
        comment.setCommentState(-1);
        //修改数据库中该评论
        return commentMapper.updateComment(comment);
    }


    /**
     * 根据评论id查询评论
     * @param id 评论id
     * @return 查询到的评论  null:没有查到
     */
    @Override
    public Comment getCommentById(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return null;
        }
        List<Comment> commentList =  commentMapper.findCommentByIds(ListUtil.addElement(id));
        if(ObjectUtil.isEmpty(commentList) || commentList.size() == 0){
            return null;
        }
        return commentList.get(0);
    }


    /**
     * 审核评论内容并修改数据库的评论状态
     * @param id 评论的id
     * @return 0：操作失败 1：操作成功
     */
    @Override
    public int auditCommentAndAlterState(Integer id) {
        if(ObjectUtil.isEmpty(id)){
            return 0;
        }
        //根据id查询该评论
        List<Comment> commentList = commentMapper.findCommentByIds(ListUtil.addElement(id));
        //如果没有查到，返回失败
        if(ObjectUtil.isEmpty(commentList) || commentList.size() == 0){
            return 0;
        }
        Comment comment = commentList.get(0);
        //审核评论
        boolean b = auditComment(comment);
        //根据审核的结果设置评论状态
        if(b){
            comment.setCommentState(1);
        }else{
            comment.setCommentState(0);
        }
        //修改数据库中该评论
        return commentMapper.updateComment(comment);
    }




    //======================================

    /**
     * 审核评论内容
     * @param comment 评论对象
     * @return true：审核通过  false：审核失败
     */
    private boolean auditComment(Comment comment){
        if (ObjectUtil.isEmpty(comment)) {
            return false;
        }
        //获取评论内容
        String content = comment.getContent();
        //审核评论内容并返回结果
        return keyWordFilter.detection(content);
    }

    //================================================================



    /**
     * 根据跟定的条件查询评论
     * @param condition 给定的条件
     * @return 查询的结果
     */
    @Override
    public List<Comment> selectByCondition(Map<String, Object> condition) {
        if(ObjectUtil.isEmpty(condition) || condition.size() == 0){
            return null;
        }
        return commentMapper.findCommentByCondition(condition);
    }

    /**
     * 获取博客下的所有评论
     * 并进行子父分类 Map<父评论,该父评论下的所有子评论>
     * @param blog 博客对象
     * @return 博客下的所有评论
     */
    @Override
    public Map<Comment, List<Comment>> getCommentsByBlog(Blog blog) {
        //查询条件（评论状态是已审核通过的，是指定博客下的）
        Map<String,Object> condition = new HashMap<>();
        condition.put("commentState",1);
        condition.put("blogId",blog.getId());
        //查询结果(该博客下的所有评论)
        List<Comment> commentList = commentMapper.findCommentByCondition(condition);
        //存储父评论id和对应的所有子评论
        Map<Integer,List<Comment>> parentIdAndSons = new HashMap<>();
        //所有的用户对象
        Map<Integer,User> userMap = TableHandler.getUserMap();
        //遍历所有评论
        for(Comment comment : commentList) {
            //是父评论
            //当评论的父评论id列为null时，说明该评论是一个父评论
            if(comment.getParentId() == -1){
                //通过父评论id从map中拿取对应的存储所有子评论的list
                //为null则new一个
                if(ObjectUtil.isEmpty(parentIdAndSons.get(comment.getId()))) {
                    parentIdAndSons.put(comment.getId(),new ArrayList<>());
                }
            }else{
                //是子评论
                //获取用户id
                int userId = comment.getCommentPersonId();
                //通过id在map中查找用户对象
                User user = userMap.get(userId);
                //修改评论对象中的用户属性的值
                comment.setUser(user);
                //通过子评论对象中的父评论id属性从map中获取对应的list
                List<Comment> sonsList = parentIdAndSons.get(comment.getParentId());
                //当获取的list为null时
                if(ObjectUtil.isEmpty(sonsList)){
                    sonsList = new ArrayList<>();
                    parentIdAndSons.put(comment.getParentId(),sonsList);
                }
                //将该评论添加到对应的list中
                sonsList.add(comment);
            }
        }
//        map集合，存储父评论和对应的所有子评论
        Map<Comment,List<Comment>> resultCommentMap = new HashMap<>();
        for(Integer parentId : parentIdAndSons.keySet()) {
            //获取该父评论的所有子评论
            List<Comment> comments = parentIdAndSons.get(parentId);
//            查询父评论对象
            Comment comment = commentMapper.findCommentByIds(ListUtil.addElement(parentId)).get(0);
            //修改父评论对象中的user属性
            comment.setUser(userMap.get(comment.getCommentPersonId()));
            resultCommentMap.put(comment,comments);
        }
        return resultCommentMap;
    }

    @Override
    public Map<Comment, List<Comment>> getCommentsByBlog(Integer blogId) {
        List<Blog> blogList = blogMapper.findBlogByIds(ListUtil.addElement(blogId));
        if(ObjectUtil.isEmpty(blogList) || blogList.size() == 0){
            return null;
        }
        return this.getCommentsByBlog(blogList.get(0));
    }

    /**
     * 添加评论
     * @param comment  评论对象
     * @return 0 ：失败  1：成功
     */
    @Override
    public int addComment(Comment comment) {
        boolean b = this.auditComment(comment);
        comment.setPublishTime(new Date());
        if(b){
            comment.setCommentState(1);
            return commentMapper.insertComment(comment);
        }
        comment.setCommentState(0);
        commentMapper.insertComment(comment);
        return 0;
    }
}
