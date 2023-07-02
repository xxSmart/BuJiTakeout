package com.xx.buji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.buji.common.CustomException;
import com.xx.buji.dto.SetmealDto;
import com.xx.buji.entity.Setmeal;
import com.xx.buji.entity.SetmealDish;
import com.xx.buji.mapper.SetmealMapper;
import com.xx.buji.service.SetmealDishService;
import com.xx.buji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * @Author: Xiangxiang_Wang
     * @Description: 新增套餐，同时保存套餐和菜品的关联关系
     * @DateTime: 2023/6/16 16:41
     * @Params:
     * @Return:
     **/
    @Transactional//保证事务一致性，全失败或者全成功
    @Override
    public void saveWithDish(SetmealDto setmealDto){
        //保存套餐的基本信息，操作Setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联关系，操作Setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 删除套餐，同时删除套餐和菜品的关联数据
    * @DateTime: 2023/6/16 22:56
    * @Params: 
    * @Return: 
    **/ 
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

        //select count(*) from setmeal where id in(1,2,3,...) and status=1;
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);//serviceImp自带的count接口
        if (count>0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in(1,2,3,...);
        LambdaQueryWrapper<SetmealDish> dishQueryWrapper=new LambdaQueryWrapper<>();
        dishQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据--setmeal_dish
        setmealDishService.remove(dishQueryWrapper);


    }
}
