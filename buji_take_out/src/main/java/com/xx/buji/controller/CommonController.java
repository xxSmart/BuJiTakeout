package com.xx.buji.controller;

import com.xx.buji.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
* @Author: Xiangxiang_Wang
* @Description: 文件的上传和下载
* @DateTime: 2023/6/13 22:59
* @Params:
* @Return:
**/
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${buji.path}")//该路径在yml配置文件中手动配置
    private String basePath;//存放到的路径，
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 文件上传
    * @DateTime: 2023/6/14 10:03
    * @Params:
    * @Return:
    **/
    @RequestMapping("/upload")
    public Result<String> upload(MultipartFile file){//这个file必须和网页上的一致，详细见幕布笔记
        //file是一个临时文件，需要转存到指定位置，否则该次请求过后便会失效。
        log.info(file.toString());

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix =originalFilename.substring(originalFilename.lastIndexOf("."));//获取该文件“.”之后的字符串，包含“.”
        //使用UUID重新生成文件名，防止文件名重复造成覆盖
        String fileName = UUID.randomUUID().toString() + suffix;//djalshl.jpg   随机生成文件名防止重名

        //创建一个目录对象
        File dir=new File(basePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  Result.success(fileName);
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description:下载
    * @DateTime: 2023/6/14 16:54
    * @Params:
    * @Return:
    **/
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流，通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            ServletOutputStream outputStream = response.getOutputStream();
            
            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes=new byte[1024];
            while((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
