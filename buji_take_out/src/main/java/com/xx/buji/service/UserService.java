package com.xx.buji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.buji.entity.User;

public interface UserService extends IService<User> {
    //发送邮箱
    void sendMsg(String to,String subject,String context);
}
