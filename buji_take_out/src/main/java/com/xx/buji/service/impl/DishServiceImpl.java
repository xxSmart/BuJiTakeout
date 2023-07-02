package com.xx.buji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.buji.dto.DishDto;
import com.xx.buji.entity.Dish;
import com.xx.buji.entity.DishFlavor;
import com.xx.buji.mapper.DishMapper;
import com.xx.buji.service.DishFlavorService;
import com.xx.buji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 新增菜品，同时保存对应的口味数据
    * @DateTime: 2023/6/15 10:03
    * @Params:
    * @Return:
    **/
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        Long dishId=dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors =flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor

        dishFlavorService.saveBatch(flavors);
    }
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 根据id查询菜品信息和对应的口味信息
    * @DateTime: 2023/6/16 12:53
    * @Params:
    * @Return:
    **/
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表中查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询口味信息，从dish_flavor中查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 更新菜品信息，同时更新对应的口味信息
    * @DateTime: 2023/6/16 13:22
    * @Params: 
    * @Return: 
    **/ 
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //更新dish_flavor口味表,先删除delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //再添加insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }


}
