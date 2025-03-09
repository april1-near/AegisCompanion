package com.smartcommunity.smart_community_platform.model.dto;


import com.smartcommunity.smart_community_platform.model.enums.Role;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserUpdateDTO {
    private Long id;
    private String username;

    private String password;
    private String phone;
    private String email;       // 需增加@TableField注解
    private Boolean isEnabled;  // 映射TINYINT(1)
    //    @Version
//    private Integer version;    // 需搭配@Version乐观锁注解
    private Role role;           // 角色：ADMIN、USER、WORKER
}
