package com.zb.blog.handler;

import com.zb.blog.entity.Tag;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类为查询tag标签服务
 * 该类是为了修改mybatis查询标签的结果为map时，
 * 将id列的值分别作为map集合的key,将查询的结果包装为tag对象作为value
 * 需要在mapper中查询的时候告知mybatis使用该处理器进行解析查询结果
 */
public class TagToMapHandler implements ResultHandler {

    //存储查询到的结果的集合
    private final Map<Integer, Tag> mappedResults = new HashMap();

    //处理查询结果的处理器
    @Override
    public void handleResult(ResultContext resultContext) {
        Map map = (Map) resultContext.getResultObject();
        Tag tag = new Tag();
        tag.setId((Integer) map.get("id"));
        tag.setTagName((String) map.get("tagName"));
        tag.setBlogCount((Integer) map.get("blogCount"));
        tag.setTagState((Integer) map.get("tagState"));
        tag.setUpdateTime((Date) map.get("updateTime"));
        tag.setCreateTime((Date) map.get("createTime"));
        mappedResults.put((Integer) map.get("id"), tag);
    }

    //返回查询结果
    public Map<Integer, Tag> getMappedResults() {
        return mappedResults;
    }

}
