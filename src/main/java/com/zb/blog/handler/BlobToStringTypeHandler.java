package com.zb.blog.handler;

import cn.hutool.core.util.ObjectUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.StandardCharsets;
import java.sql.*;

/**
 * 处理查询的时候Blob类型的乱码问题
 * 查询的时候将Blob转为String的时候使用的是IOS-8859-1，通过该类配置为UTF-8
 */
public class BlobToStringTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Blob blob = rs.getBlob(columnName);
        if(ObjectUtil.isEmpty(blob)){
            return "";
        }
        return new String(blob.getBytes(1, (int)blob.length()), StandardCharsets.UTF_8);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);
        if(ObjectUtil.isEmpty(blob)){
            return "";
        }
        return new String(blob.getBytes(1, (int)blob.length()), StandardCharsets.UTF_8);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Blob blob = cs.getBlob(columnIndex);
        if(ObjectUtil.isEmpty(blob)){
            return "";
        }
        return new String(blob.getBytes(1, (int)blob.length()), StandardCharsets.UTF_8);
    }
}
