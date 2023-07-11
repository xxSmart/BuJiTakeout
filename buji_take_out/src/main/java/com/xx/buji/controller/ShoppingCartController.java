package com.xx.buji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xx.buji.common.BaseContext;
import com.xx.buji.common.Result;
import com.xx.buji.entity.ShoppingCart;
import com.xx.buji.service.ShoppingCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
* @Author: Xiangxiang_Wang
* @Description: 购物车
* @DateTime: 2023/6/28 21:49
* @Params: 
* @Return: 
**/ 
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
@Api(tags = "套餐相关接口")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
* @Author: Xiangxiang_Wang
* @Description: 添加购物车
* @DateTime: 2023/6/28 21:52
* @Params:
* @Return:
**/

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //设置用户id，指定当前是那个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询当前菜品是否在购物车中
        Long dishId=shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if (dishId!=null)
        {//为菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId );
        }
        else {//为套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询当前菜品或套餐是否在购物车中
        ShoppingCart cartServiceOne= shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne!=null){
            //如果已存在，就在原来数据基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //如果不存在，则添加到购物车，数量默认是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartService.save(shoppingCart);
        }

        return Result.success(cartServiceOne);
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 查看购物车
    * @DateTime: 2023/6/29 18:05
    * @Params:
    * @Return:
    **/
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){

        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }


    @DeleteMapping("/clean")
    public Result<String> clean(){
        //SQL:delete from shopping_cart where user_id=?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return Result.success("已为您清空购物车");
    }

    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long dishId = shoppingCart.getDishId();
        if (dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne= shoppingCartService.getOne(queryWrapper);
        Integer number = cartServiceOne.getNumber();
        if (number>1){
            cartServiceOne.setNumber(number-1);
            shoppingCartService.updateById(cartServiceOne);
            return Result.success(shoppingCart.getName()+"已为您-1");
        }else {
            cartServiceOne.setNumber(number-1);
            shoppingCartService.removeById(cartServiceOne);
            return Result.success("已为您清空"+shoppingCart.getName()+"菜品");
        }


    }
}
