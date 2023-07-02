package com.xx.buji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.buji.common.BaseContext;
import com.xx.buji.common.CustomException;
import com.xx.buji.common.Result;
import com.xx.buji.entity.*;
import com.xx.buji.mapper.OrdersMapper;
import com.xx.buji.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * @Author: Xiangxiang_Wang
     * @Description:用户下单
     * @DateTime: 2023/6/30 21:32
     * @Params:
     * @Return:
     **/
    @Override
    @Transactional //该方法需要多次操作数据库，为保证一致性，加入该注解
    public void submit(Orders orders) {
        //获得当前用户id
        Long userId= BaseContext.getCurrentId();
        //查询当前用户的购物车数据

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        if (list == null || list.size() == 0 ){
            throw new CustomException("购物车为空，不能下单");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook=addressBookService.getById(addressBookId);
        if (addressBook==null){
            throw new CustomException("用户地址信息有误，不能下单");
        }

        //MybatisPlus提供的类
        long orderId = IdWorker.getId();//订单号
//AtomicInteger 原子操作，线程安全 可支持高并发
        AtomicInteger amount=new AtomicInteger(0);
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
           amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
           return orderDetail;
        }).collect(Collectors.toList());

        orders.setNumber(String.valueOf(orderId));
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getPhone());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName()==null ? "":addressBook.getProvinceName())+
                (addressBook.getCityName() == null ?"":addressBook.getCityName()) +
                (addressBook.getDistrictName()==null ?"":addressBook.getDistrictName())+
                (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单明细表插入数据，一条数据
        this.save(orders);
        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        shoppingCartService.remove(wrapper);
    }
}
