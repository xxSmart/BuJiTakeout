package com.xx.buji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xx.buji.common.BaseContext;
import com.xx.buji.common.Result;
import com.xx.buji.dto.DishDto;
import com.xx.buji.dto.SetmealDto;
import com.xx.buji.entity.Category;
import com.xx.buji.entity.Setmeal;
import com.xx.buji.service.CategoryService;
import com.xx.buji.service.SetmealService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @Author: Xiangxiang_Wang
* @Description: 套餐管理
* @DateTime: 2023/6/16 15:35
* @Params:
* @Return:
**/
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController{
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 新增套餐
    * @DateTime: 2023/6/16 16:37
    * @Params: 
    * @Return: 
    **/ 
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> save(@RequestBody SetmealDto setmealDto){
        log.info("提交套餐信息：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return Result.success("新增套餐成功");
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 套餐分页查询
    * @DateTime: 2023/6/16 21:33
    * @Params:
    * @Return:
    **/
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        //分页构造器对象
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");//records类型为Setmeal 所以需要单独处理成SetmealDto

        List<Setmeal> records=pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return Result.success(dtoPage);
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 删除套餐
    * @DateTime: 2023/6/16 22:52
    * @Params:
    * @Return:
    **/
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("删除dis：{}",ids);

        setmealService.removeWithDish(ids);
        return Result.success("套餐删除成功");
    }
    /**
    * @Author: Xiangxiang_Wang
    * @Description: 查询指定ID套餐信息
    * @DateTime: 2023/6/28 21:09
    * @Params:
    * @Return:
    **/
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(null!=setmeal.getCategoryId(), Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(null!=setmeal.getStatus(), Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return Result.success(list);

    }
//    @GetMapping("/{id}")
//    public Result<SetmealDto> get(@PathVariable Long id) {
//        SetmealDto setmealDto = setmealService.;
//        return Result.success(setmealDto);
//    }
}
