package com.zb.blog.mapper;

import com.zb.blog.entity.Sort;
import com.zb.blog.entity.Tag;
import com.zb.blog.entity.User;
import com.zb.blog.handler.SortToMapHandler;
import com.zb.blog.handler.TagToMapHandler;
import com.zb.blog.handler.UserToMapHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

@Repository
public class MapResultMapper extends SqlSessionDaoSupport {
    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    //查询所有分类的id和名字（）相当于正常mapper接口中的使用，只不过这个需要配置自定义的处理器
    //查询所有的分类，返回结果是id列的值作为map集合的key，sort对象作为map集合的value
    public Map<Integer, Sort> selectSortToMap(){
        SortToMapHandler handler = new SortToMapHandler();
        //第一个参数，是mapper.xml的namespace的类，和对应的id
        //第二个参数，是我们mapper.xml里的参数
        //第三个参数，我们自定义的handler
        this.getSqlSession().select(SortMapper.class.getName()+".selectSortToMap",null,handler);
        Map mappedResults = handler.getMappedResults();
        return mappedResults;
    }


    //查询所有标签的id和名字（）相当于正常mapper接口中的使用，只不过这个需要配置自定义的处理器
    //查询所有的分类，返回结果是id列的值作为map集合的key，tag对象作为map集合的value
    public Map<Integer, Tag> selectTagToMap(){
        TagToMapHandler handler = new TagToMapHandler();
        //第一个参数，是mapper.xml的namespace的类，和对应的id
        //第二个参数，是我们mapper.xml里的参数
        //第三个参数，我们自定义的handler
        this.getSqlSession().select(TagMapper.class.getName()+".selectTagToMap",null,handler);
        Map mappedResults = handler.getMappedResults();
        return mappedResults;
    }

    //查询所有标签的id和名字（）相当于正常mapper接口中的使用，只不过这个需要配置自定义的处理器
    //查询所有的分类，返回结果是id列的值作为map集合的key，tag对象作为map集合的value
    public Map<Integer, User> selectUserToMap(){
        UserToMapHandler handler = new UserToMapHandler();
        //第一个参数，是mapper.xml的namespace的类，和对应的id
        //第二个参数，是我们mapper.xml里的参数
        //第三个参数，我们自定义的handler
        this.getSqlSession().select(UserMapper.class.getName()+".selectUserToMap",null,handler);
        Map mappedResults = handler.getMappedResults();
        return mappedResults;
    }
}
