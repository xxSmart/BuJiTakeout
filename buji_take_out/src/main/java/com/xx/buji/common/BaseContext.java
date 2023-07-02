package com.xx.buji.common;

/*
 * 基于TreadLocal封装工具类，用户保存和获取当前登录用户ID
 */
public class BaseContext {
    private static ThreadLocal<Long> treadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        treadLocal.set(id);
    }
    public static Long getCurrentId(){
        return treadLocal.get();
    }
}
