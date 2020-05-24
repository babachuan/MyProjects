package com.qhc.securitydemo.filter;

import com.google.code.kaptcha.Constants;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CodeFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String uri = req.getServletPath();
        if(uri.equals("/mylogin") && req.getMethod().equalsIgnoreCase("post")){
            String sessionCode = req.getSession().getAttribute(Constants.KAPTCHA_SESSION_CONFIG_KEY).toString();
            String formCode = req.getParameter("code").trim();
            if(StringUtils.isEmpty(formCode)){
                throw new RuntimeException("验证码不能为空");
            }else if(sessionCode.equals(formCode)){
                System.out.println("验证通过");
            }else {
                throw new AuthenticationServiceException("验证不通过");
            }
        }
        filterChain.doFilter(req,resp);
    }
}
