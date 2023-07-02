package com.xx.buji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xx.buji.dto.SetmealDto;
import com.xx.buji.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 新增套餐，同时保存套餐和菜品的关联关系
    * @DateTime: 2023/6/16 17:03
    * @Params: 
    * @Return: 
    **/ 
    public void saveWithDish(SetmealDto setmealDto);
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 删除套餐，同时删除套餐和菜品的关联数据
    * @DateTime: 2023/6/16 22:56
    * @Params: 
    * @Return: 
    **/ 
    public void removeWithDish(List<Long> ids);
}
