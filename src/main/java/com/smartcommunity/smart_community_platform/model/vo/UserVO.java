package com.smartcommunity.smart_community_platform.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.smartcommunity.smart_community_platform.model.enums.Role;
import com.smartcommunity.smart_community_platform.utils.JsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

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