package com.xx.buji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.buji.common.Result;
import com.xx.buji.entity.Orders;


public interface OrdersService extends IService<Orders> {

    /**
     * @Author: Xiangxiang_Wang
     * @Description:用户下单
     * @DateTime: 2023/6/30 21:32
     * @Params:
     * @Return:
     **/
    public void submit(Orders orders);
}
