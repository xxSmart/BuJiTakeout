package com.xx.buji.common;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLIntegrityConstraintViolationException;
//全局异常处理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * @Author: Xiangxiang_Wang
     * @Description: 异常处理方法
     * @DateTime: 2023/6/10 16:26
     * @Params:
     * @Return:
     **/
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        if(exception.getMessage().contains("Duplicate entry")){
            String[] split = exception.getMessage().split(" ");
            String msg=split[2]+"已存在";
            return Result.error(msg);
        }
        return Result.error("未知错误");
    }


    /**
     * @Author: Xiangxiang_Wang
     * @Description: 删除异常处理方法
     * @DateTime: 2023/6/13 16:26
     * @Params:
     * @Return:
     **/
    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException exception) {
        log.error(exception.getMessage());
        return Result.error(exception.getMessage());
    }
}
