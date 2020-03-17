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