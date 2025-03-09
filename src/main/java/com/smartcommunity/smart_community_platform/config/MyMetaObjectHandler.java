package com.smartcommunity.smart_community_platform.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        // 自动填充 create_time 字段
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "changeTime", LocalDateTime.class, now);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 无更新时自动填充需求可留空
    }
}
