package com.xx.buji.controller;

import com.xx.buji.common.Result;
import com.xx.buji.entity.Orders;
import com.xx.buji.service.OrdersService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
@Api(tags = "下单接口接口")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * @Author: Xiangxiang_Wang
     * @Description:用户下单
     * @DateTime: 2023/6/30 21:32
     * @Params:
     * @Return:
     **/
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info("订单数据{}",orders);
        ordersService.submit(orders);
        return Result.success("下单成功");

    }
}
