package com.xx.buji.config;

import com.xx.buji.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import java.util.List;

//配置mvc框架 静态资源映射
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 设置静态资源映射
    * @DateTime: 2023/6/8 13:43
    * @Params: 
    * @Return: 
    **/
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: //扩展mvc框架的消息转换器
    * @DateTime: 2023/6/11 23:09
    * @Params:
    * @Return:
    **/
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用jackson将Java对象转换为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的信息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter); //因为是集合，里面有很多消息转换器，index设置0代表第一位使用

    }
}
