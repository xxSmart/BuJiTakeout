package com.xx.buji.filter;

import com.alibaba.fastjson.JSON;
import com.xx.buji.common.BaseContext;
import com.xx.buji.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
拦截器
检查用户是否已完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //spring提供的路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        //1.获取本次请求的URI
        String requestUri=request.getRequestURI();
        log.info("拦截到请求:{}",requestUri);
        //定义不需要处理的请求路径
        String[] urls=new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发短信
                "/user/login"   //移动端登录

        };
        //2.判断本次请求是否需要处理
        boolean check = check(requestUri, urls);
        //3.如果不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestUri);
            filterChain.doFilter(request,response);
            return;
        }
        //4.判断登录状态，若已登录则直接放行
        if (request.getSession().getAttribute("employee")!=null){
            Long empID = (Long) request.getSession().getAttribute("employee");
            log.info("登陆成功，用户id为{}",empID);

            BaseContext.setCurrentId(empID);

            filterChain.doFilter(request,response);
            return;
        }
        //4.判断登录状态，若已登录则直接放行
        if (request.getSession().getAttribute("user")!=null){
            Long userID = (Long) request.getSession().getAttribute("user");
            log.info("登陆成功，用户id为{}",userID);

            BaseContext.setCurrentId(userID);

            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录!");
        //5.未登录 则返回未登录结果,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

    }
/**
* @Author: Xiangxiang_Wang
* @Description: 路径匹配，检查本次请求是否需要放行
* @DateTime: 2023/6/8 22:32
* @Params: requestURI,urls
* @Return:
**/
    public boolean check(String requestURI,String[] urls){
        for (String url : urls) {
           boolean match= PATH_MATCHER.match(url,requestURI);
           if (match){
               return true;
           }
        }
        return false;
    }

}
