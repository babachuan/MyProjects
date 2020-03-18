package com.qhc.oa.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {
    //不需要验证登录的uri
    private final String[] IGNORE_URI = {"/index", "/account/login", "/css/", "/js", "/images", "/account/validataAccount"};
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        //转换成HttpServletRequest 和HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //获取URI
        String uri = request.getRequestURI();
        System.out.println("当前访问的uri:" + uri);
        //判断是不是在忽略列表里
        boolean pass = canPass(uri);
        if (pass) {
            filterChain.doFilter(request, response);
            return;
        }
        //然后如果不在忽略列表，判断是否已经登录
        //如果没有登录，强制跳转到登录页面
        Object account = request.getSession().getAttribute("account");
        System.out.println("getSession account:" + account);
        if (null == account) {//没有登录则跳转到登录页面
            response.sendRedirect("/account/login");
            return;//这里记住要返回
        }
        //如果已经登录，则放行
        filterChain.doFilter(request, response);
    }
    private boolean canPass(String uri) {
        for (String val : IGNORE_URI) {
            //判断当前的URI是不是以上面忽略列表开头的，如果是就放过
            if (uri.startsWith(val)) {
                return true;
            }
        }
        //否则拦截
        return false;
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter init---------");
        Filter.super.init(filterConfig);
    }
}
