package com.xx.buji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.buji.common.CustomException;
import com.xx.buji.entity.Category;
import com.xx.buji.entity.Dish;
import com.xx.buji.entity.Setmeal;
import com.xx.buji.mapper.CategoryMapper;
import com.xx.buji.service.CategoryService;
import com.xx.buji.service.DishService;
import com.xx.buji.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 根据id删除分类，删除之前需要进行判断
    * @DateTime: 2023/6/12 23:12
    * @Params: 
    * @Return: 
    **/ 
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishQueryWrapper);
        //查询当前分类是否关联了菜品
        if (count1>0){
            //已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类项关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealQueryWrapper);
        if (count2>0) {
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类项关联了套餐，不能删除");
        }

        //都未关联，正常删除
        super.removeById(id);
    }
}
