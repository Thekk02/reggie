package com.kk.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.kk.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author kk
 * @description 登录拦截器,检查用户是否已经完成登录
 * @date 2023-08-26 11:38:51
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
    * @Description:拦截未登录或者直接访问资源的请求
    * @Params:
    * @Return:
    */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取本次请求的url
        String requestURL = request.getRequestURI();
        log.info("拦截到请求：{}",requestURL);
        
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        
        //2.判断本次请求的URL是否需要处理
        boolean check = check(urls,requestURL);

        //3.如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURL);
            filterChain.doFilter(request,response);
            return;
        }

        //4.判断登录状态，如果已经登陆，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已经登录，用户id为{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录则返回未登陆结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * @Description: 路径匹配，检查本次请求是否需要放行
     * @Params:
     * @Return: void
     */
    public boolean check(String[] urls,String requestURL){
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURL);
            if(match){return true;}
        }
        return false;
    }
}
