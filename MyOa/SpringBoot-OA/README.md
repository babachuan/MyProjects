[toc]

# 1.数据库准备

创建账号数据库`account`

```
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loginname` varchar(255) UNIQUE NOT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `age` int(10) DEFAULT NULL COMMENT '年龄',
  `location` varchar(255) DEFAULT NULL COMMENT '国籍',
  `role` varchar(255) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

# 2.代码准备

代码的ORM框架使用的是Mybatis，提前针对需要用到的接口和mapper用工具，生成，这里用到的工具是：

[mybatis-generator-gui](https://github.com/zouzg/mybatis-generator-gui)

具体使用参见github.

# 3.登录核心代码

在页面上对登录的核心验证代码参见如下javascript

```
$(function(){	
			$(".login_btn").click(function(){
				var loginName = $("#loginName").val();
				var password = $("#password").val();
				if(loginName=="" || password ==""){
					$(".tip").html("用户名和密码不能为空");
					$(".tip").css("display","block");
					return;
				}else{
					// 异步校验账号密码
					var url = "/account/validataAccount";
					var args = {loginName:loginName,password:password};
					$.post(url,args,function(data){
						if(data == "success"){
							// 登录成功 跳转页面
							window.location.href="/account/";
						}else {
							$(".tip").html("用户名或密码错误");
							$(".tip").css("display","block");
						}
						console.log(data)
					});
				}
			})
						
		})
```

在这里通过使用jQuery的`$.post(url,args,function(data)`进行异步校验，如果返回的参数是`success`（这个标识是自定义的）则通过js代码`window.location.href="/account/"`进行跳转验证。

**Controller端代码**

```
    @RequestMapping("/validataAccount")
    @ResponseBody
    public String validataAccount(String loginName,
                                  String password,
                                  HttpServletRequest request){
        System.out.println("loginName:"+loginName);
        System.out.println("password:"+password);
        Account account = accountService.findByLoginNameAndPassword(loginName,password);
        if(account == null){
            return "登录失败";
        }else{
            //通过请求获取session
            request.getSession().setAttribute("account",account);
            return "success";
        }
    }
```

这里通过jQuery调用时会把参数带过来。

# 4.URI权限校验

在访问网站时如果没有登录，有些资源页面是不准访问的，这时候就需要通过URI进行校验。对应代码如下

```
package com.qhc.oa.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*") //这里是对所有的URI做拦截校验
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
```

整体的逻辑是这样：

- 第一步：判断访问的URI是否在忽略列表（这个忽略列表就是让用户去访问登录的，所以不能屏蔽）
- 第二步：如果访问的URI没在忽略列表，那么判断是否该用户登录过，如果没登录，强制跳转到登录页面
- 第三步：如果第二步中的账号登录过，那么放行，允许访问

