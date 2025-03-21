package com.aegis.companion.model.entity;

import com.aegis.companion.model.enums.Role;
import com.aegis.companion.utils.JsonTypeHandler;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("user") // 明确指定表名
@Accessors(chain = true) // ← 关键注解
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    @TableField("password_hash")
    private String password;

    private String phone;

    @TableField("email") // 如果列名是 "email"，可省略
    private String email;

    @TableField(value = "is_enabled", jdbcType = JdbcType.TINYINT)
    private Boolean isEnabled;

    @Version
    @TableField("version") // 如果列名是 "email"，可省略
    private Integer version; // 乐观锁

    @TableField(value = "role", typeHandler = EnumTypeHandler.class) // 关键修复：指定枚举处理器
    private Role role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(typeHandler = JsonTypeHandler.class)
    private List<String> skills;

    @TableField("current_load")
    private Integer currentLoad;


    public boolean isAdmin() {
        return role == Role.ADMIN || role == Role.SUPER_ADMIN;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
