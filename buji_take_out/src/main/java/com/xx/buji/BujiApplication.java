package com.xx.buji;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching // 开启spring cache注解的缓存功能
public class BujiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BujiApplication.class,args);
        log.info("启动成功。。。");
    }
}
