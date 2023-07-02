package com.xx.buji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xx.buji.common.Result;
import com.xx.buji.dto.DishDto;
import com.xx.buji.entity.Category;
import com.xx.buji.entity.Dish;
import com.xx.buji.entity.DishFlavor;
import com.xx.buji.service.CategoryService;
import com.xx.buji.service.DishFlavorService;
import com.xx.buji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Xiangxiang_Wang
 * @Description: 菜品管理
 * @DateTime: 2023/6/14 18:32
 * @Params:
 * @Return:
 **/
@RequestMapping("/dish")
@RestController
@Slf4j
public class DishController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * @Author: Xiangxiang_Wang
     * @Description: 新增菜品
     * @DateTime: 2023/6/14 23:58
     * @Params:
     * @Return:
     **/
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    /**
     * @Author: Xiangxiang_Wang
     * @Description: 菜品信息分页查询
     * @DateTime: 2023/6/15 19:15
     * @Params:
     * @Return:
     **/
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");//将pageInfo拷贝到dishDtoPage

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {//此时item就是Dish
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类ID
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }

    /**
     * @Author: Xiangxiang_Wang
     * @Description: 根据id查询菜品信息和对应的口味信息
     * @DateTime: 2023/6/16 12:51
     * @Params:
     * @Return:
     **/
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }


    /**
     * @Author: Xiangxiang_Wang
     * @Description: 修改菜品并保存
     * @DateTime: 2023/6/14 23:58
     * @Params:
     * @Return:
     **/
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return Result.success("菜品修改成功");
    }

    /**
     * @Author: Xiangxiang_Wang
     * @Description: 根据条件查询对应的菜品
     * @DateTime: 2023/6/16 16:21
     * @Params:
     * @Return:
     **/
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish) {
//        //添加查询构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        //添加查询条件,查询状态为1（在售状态）的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return Result.success(list);
//    }
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        //添加查询构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加查询条件,查询状态为1（在售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item) -> {//此时item就是Dish
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();//分类ID
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId= item.getId();//当前菜品的ID
            LambdaQueryWrapper<DishFlavor> flavorQueryWrapper=new LambdaQueryWrapper();

            flavorQueryWrapper.eq(DishFlavor::getDishId,dishId);
            // SQL:select * frome dish_flavor where dish_id=?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return Result.success(dishDtoList);

    }

}