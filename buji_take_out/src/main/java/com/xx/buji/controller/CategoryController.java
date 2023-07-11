package com.xx.buji.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xx.buji.common.Result;
import com.xx.buji.entity.Category;
import com.xx.buji.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
@Api(tags = "菜品分类相关接口")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 新增分类
    * @DateTime: 2023/6/12 22:18
    * @Params:
    * @Return:
    **/
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("category：{}",category);
        categoryService.save(category);
        return Result.success("新增分类成功！");

    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 分页查询
    * @DateTime: 2023/6/12 22:28
    * @Params:
    * @Return:
    **/
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize){
        log.info("分类分页查询");
        //分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //分页查询
        categoryService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 根据id删除分类
    * @DateTime: 2023/6/12 22:48
    * @Params:
    * @Return:
    **/
    @DeleteMapping
    public Result<String> delete(Long ids){
        log.info("删除分类,id为:{}",ids);

//        categoryService.removeById(ids);
        categoryService.remove(ids);

        return Result.success("分类信息删除成功");
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description: 根据id修改分类信息
    * @DateTime: 2023/6/13 15:50
    * @Params:
    * @Return:
    **/
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return Result.success("修改分类信息成功");
    }

    /**
    * @Author: Xiangxiang_Wang
    * @Description:根据条件查询分类数据
    * @DateTime: 2023/6/14 22:54
    * @Params:
    * @Return:
    **/
    @GetMapping("/list")
    public Result<List<Category>> list(Category category){
        log.info("根据条件查询分类数据");
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
