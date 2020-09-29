package com.zb.blog.handler;

import com.zb.blog.entity.Sort;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类为查询sort分类服务
 * 该类是为了修改mybatis查询分类的结果为map时，
 * 将id列的值分别作为map集合的key,将查询的结果包装为sort对象作为value
 * 需要在mapper中查询的时候告知mybatis使用该处理器进行解析查询结果
 */
public class SortToMapHandler implements ResultHandler {

    //存储查询结果的集合
    private final Map<Integer, Sort> mappedResults = new HashMap();

    //处理查询结果的处理器
    @Override
    public void handleResult(ResultContext resultContext) {
        Map map = (Map) resultContext.getResultObject();
        // xml 配置里面的property的值，对应的列
        Sort sort = new Sort();
        sort.setId((Integer) map.get("id"));
        sort.setSortName((String) map.get("sortName"));
        sort.setBlogCount((Integer) map.get("blogCount"));
        sort.setSortAbstract((String) map.get("sortAbstract"));
        sort.setSortState((Integer) map.get("sortState"));
        sort.setUpdateTime(((Date) map.get("updateTime")));
        sort.setCreateTime(((Date) map.get("createTime")));
        mappedResults.put((Integer) map.get("id"), sort);
    }
    //返回查询结果
    public Map getMappedResults() {
        return mappedResults;
    }
}
