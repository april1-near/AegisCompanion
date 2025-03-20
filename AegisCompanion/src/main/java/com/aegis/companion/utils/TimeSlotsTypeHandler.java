package com.aegis.companion.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aegis.companion.model.enums.TimeSlotStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;

public class TimeSlotsTypeHandler extends BaseTypeHandler<Map<String, TimeSlotStatusEnum>> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Map<String, TimeSlotStatusEnum> parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("JSON序列化失败", e);
        }
    }


    @Override
    public Map<String, TimeSlotStatusEnum> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        try {
            // 从BLOB字段读取字节流
            Blob blob = rs.getBlob(columnName);
            if (blob == null) return null;
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return parseJson(new String(bytes, StandardCharsets.UTF_8));
        } catch (SQLException e) {
            throw new SQLException("读取BLOB字段失败: " + columnName, e);
        }
    }

    @Override
    public Map<String, TimeSlotStatusEnum> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public Map<String, TimeSlotStatusEnum> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private Map<String, TimeSlotStatusEnum> parseJson(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new java.util.HashMap<>();
            }
            return mapper.readValue(json,
                    new TypeReference<Map<String, TimeSlotStatusEnum>>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败: " + json, e);
        }
    }
}
