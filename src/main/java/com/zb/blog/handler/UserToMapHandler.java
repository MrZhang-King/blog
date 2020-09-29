package com.zb.blog.handler;

import com.zb.blog.entity.User;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 该类为查询user用户服务
 * 该类是为了修改mybatis查询用户的结果为map时，
 * 将id列的值分别作为map集合的key,将查询的结果包装为user对象作为value
 * 需要在mapper中查询的时候告知mybatis使用该处理器进行解析查询结果
 */
public class UserToMapHandler implements ResultHandler {

    //存储查询结果的集合
    private final Map<Integer, User> mappedResults = new HashMap();

    //处理查询结果的处理器
    @Override
    public void handleResult(ResultContext resultContext) {
        Map map = (Map) resultContext.getResultObject();
        // xml 配置里面的property的值，对应的列
       User user = new User();
       user.setId((Integer) map.get("id"));
       user.setIcon((String) map.get("icon"));
       user.setManagerState((Integer) map.get("managerState"));
       user.setPassword((String) map.get("password"));
       user.setUsername((String) map.get("username"));
        mappedResults.put((Integer) map.get("id"), user);
    }
    //返回查询结果
    public Map getMappedResults() {
        return mappedResults;
    }
}
