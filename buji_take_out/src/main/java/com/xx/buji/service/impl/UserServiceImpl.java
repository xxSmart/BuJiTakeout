package com.xx.buji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.buji.entity.User;
import com.xx.buji.mapper.UserMapper;
import com.xx.buji.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${spring.mail.username}")
    private String from;//application.yml中获取邮件发送人

    @Autowired
    private JavaMailSender mailSender;
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 发送邮箱
    * @DateTime: 2023/6/18 14:57
    * @Params: 
    * @Return: 
    **/ 
    @Override
    public void sendMsg(String to, String subject, String context) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(context);
        //发送邮件操作，从from到to
        mailSender.send(mailMessage);
    }
    }
