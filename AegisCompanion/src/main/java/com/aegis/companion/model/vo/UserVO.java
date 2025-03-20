package com.aegis.companion.model.vo;

import com.aegis.companion.model.enums.Role;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private boolean isEnabled;
    private LocalDateTime createTime;
//    @TableField(typeHandler = JsonTypeHandler.class)
//    private List<String> skills;
}