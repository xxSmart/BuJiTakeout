package com.xx.buji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xx.buji.common.Result;
import com.xx.buji.entity.User;
import com.xx.buji.service.UserService;
import com.xx.buji.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 发送手机短信验证码
    * @DateTime: 2023/6/17 16:04
    * @Params: 
    * @Return: 
    **/ 
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取邮箱
        String phone=user.getPhone();
        String subject="步急外卖登录验证码";
        if (StringUtils.isNotEmpty(phone)){
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String context = "欢迎使用步急外卖，登录验证码为："+code+"，请妥善保管（验证码五分钟内有效）!";
            log.info("code={}",code);
            //调用阿里云提供的短信服务API发送短信
          //  SMSUtils.sendMessage("步急外卖","",phone,code);
            //发送邮箱验证码
            userService.sendMsg(phone,subject,context);
            //将验证码保存在Session中
            //session.setAttribute(phone,code);
            //验证码由保存到session优化为redis，并设置有效期为分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return Result.success("邮箱验证码发送成功，请及时查看");
        }else
        return Result.error("验证码发送失败");
    }
    
    
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 移动端用户登录
    * @DateTime: 2023/6/17 16:22
    * @Params: 
    * @Return: 
    **/ 
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获得手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);
        //从redis中获取验证码
        Object codeInSession= redisTemplate.opsForValue().get(phone);
        //用提交的验证码和已保存的验证码进行验证码比对
        if (code.equals(codeInSession)){//codeInSession!=null&&codeInSession.equals(code)
            //如果比对一致，则登陆成功
            //判断当前手机号是否为新用户，如果是新用户自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                //为空证明是新用户，自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            //用户登录成功，删除缓存中的验证码
            redisTemplate.delete(phone);
            return Result.success(user);
        }
        return Result.error("登陆失败，邮箱或验证码不正确");
    }
}
