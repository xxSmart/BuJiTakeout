package com.xx.buji.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
* @Author: Xiangxiang_Wang
* @Description: 自定义元数据对象处理器
* @DateTime: 2023/6/12 12:54
* @Params: 
* @Return: 
**/
@Slf4j
@Component//注解功能:spring框架来管理
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 插入时使用
    * @DateTime: 2023/6/12 12:53
    * @Params: 
    * @Return: 
    **/ 
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",new Long(BaseContext.getCurrentId()));
        metaObject.setValue("updateUser",new Long(BaseContext.getCurrentId()));
    }
/**
* @Author: Xiangxiang_Wang
* @Description: 更新和修改时使用
* @DateTime: 2023/6/12 12:54
* @Params:
* @Return:
**/
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",new Long(BaseContext.getCurrentId()));
    }
}
