// JsonTypeHandler.java
package com.smartcommunity.smart_community_platform.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("JSON序列化失败", e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }


    private List<String> parseJson(String json) {
        // 处理空值情况
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("JSON解析失败，原始内容：{}", json, e);
            return new ArrayList<>();
        }
    }
}
