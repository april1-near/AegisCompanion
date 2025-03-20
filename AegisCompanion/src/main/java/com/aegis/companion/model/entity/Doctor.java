package com.aegis.companion.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

// Doctor.java
@Data
@Accessors(chain = true)
@TableName("doctor")
public class Doctor {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;        // 姓名/Nom
    private String title;       // 职称/Titre
    private String introduction;// 简介/Introduction
}
