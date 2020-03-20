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

# 翻页

翻页使用的组件时PageHelper

**Controller中的代码**

```
    @RequestMapping("list")
    public String accountList(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model){
        PageInfo<Account> page = accountService.findByPage(pageNum,pageSize);
        System.out.println("==============="+page);
        model.addAttribute("accountList", page);
        return "account/list";
    }
```

在controller里接收一个PageInfo对象，传进model里`model.addAttribute("accountList", page)`

**Service代码**

```
    public PageInfo<Account> findByPage(int pageNum, int pageSize) {
        //启动分页
        PageHelper.startPage(pageNum,pageSize);
        //查询出所有数据，并通过PageInfo接收
        AccountExample example = new AccountExample();
        List<Account> accounts = accountMapper.selectByExample(example);
        //这里传的参数5是为了后面显示5页
        PageInfo<Account> pageInfo = new PageInfo<>(accounts,5);
        return  pageInfo;
    }
```

使用`PageHelper.startPage(pageNum,pageSize)`启动分页查询，这里用到AOP知识。

这里使用了PageInfo,这个在thymleaf中大大简化了翻页操作。

**list.html代码**

```
<table class="table table-hover">
    <tr>
        <th>序号</th>
        <th>工号</th>
        <th>账户</th>
        <th>昵称</th>
        <th>年龄</th>
        <th>国籍</th>
        <th>角色</th>
        <th>操作</th>
    </tr>

    <tr th:each="account:${accountList.list}">
        <td th:text="${accountStat.count}"></td>
        <td th:text="${account.id}">工号</td>
        <td th:text="${account.loginname}">账户</td>
        <td th:text="${account.nickname}">昵称</td>
        <td th:text="${account.age}">年龄</td>
        <td th:text="${account.location}">国籍</td>
        <td th:text="${account.role}">角色</td>
        <td>操作</td>
    </tr>
</table>
<!--列表-->

<!--调试代码,获取PageInfo中的所有数据-->
<!--[[${accountList.navigatepageNums}]]-->
<!--调试代码-->

<!--翻页-->
<nav aria-label="Page navigation">
    <ul class="pagination">
        <li th:class="${accountList.prePage}==0?'disabled':'' ">
            <a th:href="@{${accountList.prePage}== 0?'javascript:void(0);' :'/account/list?pageNum='+${accountList.prePage}}"
               aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>

        <!--通过循环迭代出5页的内容--><!--如果是当前页，就active   -->
        <li th:class="${accountList.pageNum}==${pageNum}?'active':'#'"
            th:each="pageNum : ${accountList.navigatepageNums}">
            <!--调试代码：[[${accountList.pageNum}]] [[${pageNum}]]-->
            <a th:href="@{'/account/list?pageNum='+${pageNum}}">[[${pageNum}]]</a>
        </li>
        <!--静态写法<li><a th:href="@{/account/list?pageNum=2}">2</a></li>-->
        <li th:class="${accountList.isLastPage}==true ?'disabled':''">
            <a th:href="@{${accountList.isLastPage}==true ? 'javascript:void(0);':'/account/list?pageNum='+${accountList.nextPage}}"
               aria-label="Next">
                <span aria-hidden="true">&raquo;</span>
            </a>
        </li>
    </ul>
</nav>
```

