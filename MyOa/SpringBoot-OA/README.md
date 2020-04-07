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

# 5.翻页

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

`PageInfo<Account> pageInfo = new PageInfo<>(accounts,5);`这里指定显示5个页码

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

`th:each="account:${accountList.list}"`是使用PageInfo对象中的`list`方法获取对象数组，然后进行循环。

翻页是使用的bootstrap组件，其中关键是上一页、页码、下一页的说明

**页码表格**

```
        <li th:class="${accountList.pageNum}==${pageNum}?'active':'#'"
            th:each="pageNum : ${accountList.navigatepageNums}">
            <a th:href="@{'/account/list?pageNum='+${pageNum}}">[[${pageNum}]]</a>
        </li>
```

首先使用`th:each="pageNum : ${accountList.navigatepageNums}`循环获取指定页码数，

如果点中的是当前页，那么当前页高亮`th:class="${accountList.pageNum}==${pageNum}?'active':'#'"`

下面是打印的PageInfo信息,当前页码是1，对应的`navigatepageNums=[1, 2, 3, 4, 5]}`

```
PageInfo{pageNum=1, pageSize=5, size=5, startRow=1, endRow=5, total=28, pages=6, list=Page{count=true, pageNum=1, pageSize=5, startRow=0, endRow=5, total=28, pages=6, reasonable=false, pageSizeZero=false}[Account [Hash = -2109168208, id=1, loginname=sunwukong, password=s123456, nickname=孙悟空, age=100, location=中原, role=大师兄, serialVersionUID=1], Account [Hash = 1721357063, id=2, loginname=zhubajie, password=z123456, nickname=猪八戒, age=100, location=中土, role=二师兄, serialVersionUID=1], Account [Hash = -40731312, id=3, loginname=zhubajie31, password=z123456, nickname=猪八戒1, age=100, location=中土1, role=二师兄2, serialVersionUID=1], Account [Hash = 1734276051, id=4, loginname=zhubajie41, password=z123456, nickname=猪八戒1, age=100, location=中土1, role=二师兄3, serialVersionUID=1], Account [Hash = -785683882, id=5, loginname=zhubajie51, password=z123456, nickname=猪八戒1, age=100, location=中土1, role=二师兄4, serialVersionUID=1]], prePage=0, nextPage=2, isFirstPage=true, isLastPage=false, hasPreviousPage=false, hasNextPage=true, navigatePages=5, navigateFirstPage=1, navigateLastPage=5, navigatepageNums=[1, 2, 3, 4, 5]}
```

然后通过`<a th:href="@{'/account/list?pageNum='+${pageNum}}">[[${pageNum}]]</a>`这样的\<a\>标签进行显示。

**上一页/下一页**

```
        <li th:class="${accountList.prePage}==0?'disabled':'' ">
            <a th:href="@{${accountList.prePage}== 0?'javascript:void(0);' :'/account/list?pageNum='+${accountList.prePage}}"
               aria-label="Previous">
                <span aria-hidden="true">&laquo;</span>
            </a>
        </li>
```

这里有两个逻辑判断`th:class="${accountList.prePage}==0?'disabled':''`如果上一页是0，那么这个上一页标签不允许再点击，

`th:href="@{${accountList.prePage}== 0?'javascript:void(0);' :'/account/list?pageNum='+${accountList.prePage}}"`判断上页是不是0，如果是就用`javascript:void(0);`代码进行限定，不让点击（上面只是显示禁用图标，但依然可以点击），如果不是0，就进行上页跳转`'/account/list?pageNum='+${accountList.prePage}`,下一页的逻辑判断同这个。

# 6.删除记录

操作：在页面上点击删除按钮，删除一条记录

**前端页面**

```
            <a class="btn" data-toggle="modal" data-target="#deleteByIdModal" th:href="@{'javascript:deleteById('+${account.id}+');'}">删除</a>
```

**Javascript代码**

```
    // 删除记录
    function deleteById(id) {
        console.log("id:" + id)

        $(function() {
            $('#deleteByIdModal').modal('hide')
        });

            var url = "/account/deleteById";
            var args = {id: id};
            $.post(url, args, function (data) {
                    console.log(data)
                    if (data.code == 200) {
                        window.location.reload();
                    } else {
                        alert(data.msg)
                    }
                }
            );
    }
```

`var url = "/account/deleteById"`可以看到转到的crontroller接口。通过`jQuery`的`post`异步提交请求`$.post(url, args, function (data)..)`进行提交。

**Controller**

```
    //删除记录
    @RequestMapping("deleteById")
    @ResponseBody
    public RespState accountList(@RequestParam int id){
        RespState respState = accountService.deleteById(id);
        return respState;
    }
```

这里使用`RespState`接收操作结果。

**Service代码**

```
    public RespState deleteById(int id) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andIdEqualTo(id);
        int row = accountMapper.deleteByExample(accountExample);
        if(row == 1){
            return RespState.build(200);
        }else{
            return RespState.build(500,"删除出错");
        }
    }
```

这里使用Mybatis进行操作。

上面的操作比较简单，具体参见代码。

# 7.数据库SQL

```
/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80011
Source Host           : localhost:3306
Source Database       : oa

Target Server Type    : MYSQL
Target Server Version : 80011
File Encoding         : 65001

Date: 2020-03-24 08:32:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account
-- ----------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loginname` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `age` int(10) DEFAULT NULL COMMENT '年龄',
  `location` varchar(255) DEFAULT NULL COMMENT '国籍',
  `role` varchar(255) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`),
  UNIQUE KEY `loginname` (`loginname`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account
-- ----------------------------
INSERT INTO `account` VALUES ('1', 'sunwukong', 's123456', '孙悟空', '100', '中原', '大师兄');
INSERT INTO `account` VALUES ('2', 'zhubajie', 'z123456', '猪八戒', '102', '东土大唐', '二师兄');
INSERT INTO `account` VALUES ('3', 'shaseng', 's123456', '沙僧', '103', '唐朝', '三师弟');
INSERT INTO `account` VALUES ('4', 'tangseng', 't123456', '唐僧', '50', '唐国', '师傅');
INSERT INTO `account` VALUES ('5', 'tangseng2', 't123456', '唐僧1', '51', '唐国1', '师傅2');
INSERT INTO `account` VALUES ('6', 'tangseng3', 't123456', '唐僧1', '51', '唐国1', '师傅3');
INSERT INTO `account` VALUES ('7', 'tangseng4', 't123456', '唐僧1', '51', '唐国1', '师傅4');
INSERT INTO `account` VALUES ('8', 'tangseng5', 't123456', '唐僧1', '51', '唐国1', '师傅5');
INSERT INTO `account` VALUES ('9', 'tangseng6', 't123456', '唐僧1', '51', '唐国1', '师傅6');
INSERT INTO `account` VALUES ('10', 'tangseng7', 't123456', '唐僧1', '51', '唐国1', '师傅7');
INSERT INTO `account` VALUES ('11', 'tangseng8', 't123456', '唐僧1', '51', '唐国1', '师傅8');
INSERT INTO `account` VALUES ('12', 'tangseng9', 't123456', '唐僧1', '51', '唐国1', '师傅9');
INSERT INTO `account` VALUES ('13', 'tangseng10', 't123456', '唐僧1', '51', '唐国1', '师傅10');
INSERT INTO `account` VALUES ('14', 'tangseng11', 't123456', '唐僧1', '51', '唐国1', '师傅11');
INSERT INTO `account` VALUES ('15', 'tangseng12', 't123456', '唐僧1', '51', '唐国1', '师傅12');
INSERT INTO `account` VALUES ('16', 'tangseng13', 't123456', '唐僧1', '51', '唐国1', '师傅13');
INSERT INTO `account` VALUES ('17', 'tangseng14', 't123456', '唐僧1', '51', '唐国1', '师傅14');
INSERT INTO `account` VALUES ('18', 'tangseng15', 't123456', '唐僧1', '51', '唐国1', '师傅15');
INSERT INTO `account` VALUES ('19', 'tangseng16', 't123456', '唐僧1', '51', '唐国1', '师傅16');
INSERT INTO `account` VALUES ('20', 'tangseng17', 't123456', '唐僧1', '51', '唐国1', '师傅17');
INSERT INTO `account` VALUES ('21', 'tangseng18', 't123456', '唐僧1', '51', '唐国1', '师傅18');
INSERT INTO `account` VALUES ('22', 'tangseng19', 't123456', '唐僧1', '51', '唐国1', '师傅19');
INSERT INTO `account` VALUES ('23', 'tangseng20', 't123456', '唐僧1', '51', '唐国1', '师傅20');
INSERT INTO `account` VALUES ('24', 'tangseng21', 't123456', '唐僧1', '51', '唐国1', '师傅21');
INSERT INTO `account` VALUES ('25', 'tangseng22', 't123456', '唐僧1', '51', '唐国1', '师傅22');
INSERT INTO `account` VALUES ('26', 'tangseng23', 't123456', '唐僧1', '51', '唐国1', '师傅23');
INSERT INTO `account` VALUES ('27', 'tangseng24', 't123456', '唐僧1', '51', '唐国1', '师傅24');
INSERT INTO `account` VALUES ('28', 'tangseng25', 't123456', '唐僧1', '51', '唐国1', '师傅25');
INSERT INTO `account` VALUES ('29', 'tangseng26', 't123456', '唐僧1', '51', '唐国1', '师傅26');
INSERT INTO `account` VALUES ('30', 'tangseng27', 't123456', '唐僧1', '51', '唐国1', '师傅27');
INSERT INTO `account` VALUES ('31', 'tangseng28', 't123456', '唐僧1', '51', '唐国1', '师傅28');
INSERT INTO `account` VALUES ('32', 'tangseng29', 't123456', '唐僧1', '51', '唐国1', '师傅29');
INSERT INTO `account` VALUES ('33', 'tangseng30', 't123456', '唐僧1', '51', '唐国1', '师傅30');
INSERT INTO `account` VALUES ('34', 'tangseng31', 't123456', '唐僧1', '51', '唐国1', '师傅31');
INSERT INTO `account` VALUES ('35', 'tangseng32', 't123456', '唐僧1', '51', '唐国1', '师傅32');
INSERT INTO `account` VALUES ('36', 'tangseng33', 't123456', '唐僧1', '51', '唐国1', '师傅33');
INSERT INTO `account` VALUES ('37', 'tangseng34', 't123456', '唐僧1', '51', '唐国1', '师傅34');

-- ----------------------------
-- Table structure for account_role
-- ----------------------------
DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of account_role
-- ----------------------------

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pname` varchar(255) DEFAULT NULL,
  `uri` varchar(255) DEFAULT NULL,
  `c` tinyint(2) DEFAULT NULL,
  `r` tinyint(2) DEFAULT NULL,
  `u` tinyint(2) DEFAULT NULL,
  `d` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES ('1', '系统管理', '/system/manager', null, null, null, null);
INSERT INTO `permission` VALUES ('2', '实验管理', '/trial/management', null, null, null, null);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `roleName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roleName_uniq` (`roleName`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('00000000002', '前台用户');
INSERT INTO `role` VALUES ('00000000001', '管理员');

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `permission_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES ('1', '1', '1');
INSERT INTO `role_permission` VALUES ('2', '2', '2');
SET FOREIGN_KEY_CHECKS=1;

```

# 8.提交Post报错处理

在提交修改表单请求的时候，参数是使用checkbox进行赋值，在html模板中的代码如下：

```
        formObject["c"] = $("#c").is(':checked');
        formObject["r"] = $("#r").is(':checked');
        formObject["u"] = $("#u").is(':checked');
        formObject["d"] = $("#d").is(':checked');
```

这样赋值得到的json串如下：

```
formObject{"name":"实验管理","uri":"/trial/management2","id":"2","c":true,"r":false,"u":false,"d":true}
```

发现对应的值是`true`的类型，在传入接口后就会报错。

**原因**：在页面上传到后台的参数类型与页面的contentType类型不匹配。一定检查传的参数是否符合后台接收要求！！

**解决方法**

```
 formObject["c"] = $("#c").is(':checked')?"1":"0";
 formObject["r"] = $("#r").is(':checked')?"1":"0";
 formObject["u"] = $("#u").is(':checked')?"1":"0";
 formObject["d"] = $("#d").is(':checked')?"1":"0";
```

使用javascript的三目运算符进行转换，将`true`转成"1"，`false`转成"0"，然后再进行提交，测试结果OK。

修改后得到的json串：

```
formObject{"name":"实验管理","uri":"/trial/management2","id":"2","c":"1","r":"0","u":"0","d":"1"}
```

# 9.checkbox标签设置属性

在给角色关联权限的时候，需要判定当前权限是不是已经关联过。

**解决思路**：

获取到全部的权限信息；

获取当前角色已经关联的权限信息；

分别进行判断，如果当前权限已经被关联过，则直接标记显示。

**HTML中权限名称部分的代码**

```
权限名称：
<span th:each="item : ${permissionList}">
    <input type="checkbox" th:id="${item.id}" name="permissions" th:value="${item.id}" onclick="check()">
    <label>[[${item.pname}]]</label>
</span>
```

在关联权限的controller跳转页面代码如下：

```
    @RequestMapping("rolePermission")
    public String rolePermission(@RequestParam int id, Model model){
        Role role = roleService.findById(id);

        //角色的权限id列表
        List<Permission> rpList = roleService.getRolePermission(id);
        List<Permission> permissionList = permissionService.findAll();
        model.addAttribute("aList",rpList);
        model.addAttribute("role",role);
        model.addAttribute("permissionList",permissionList);
        return "manager/rolePermission";
    }
```

这里分别获取`rpList`已经关联的权限列表和全部的权限列表`permissionList`,然后传到前端页面。

**前端比较的代码**

```
    //这里的aList是获取到的角色的权限列表
    for (accPermision in [[${aList}]]){

        //获取角色中权限列表的具体的列表值,可以看成permission对象
        console.log([[${aList}]][accPermision])
        var pList = [[${aList}]][accPermision]["permissionList"]
        for(permission in pList){
            console.log(pList[permission])
            // 根据获取的权限的id，判断当前角色是不是已经关联，如果关联那么通过jQuery标记选择。
            for (item in [[${permissionList}]]) {
                var pid = [[${permissionList}]][item].id
                if(pList[permission]["id"] == pid){
                    $("#"+pid).prop("checked", true);
                    // console.log(pid)

                }
            }
        }
    }
```

这里重点是使用jQuery设定checkbox的属性`$(#id).prop('checked', true);`来通过判断结果设定。

# 10.权限校验

在设定用户角色和对应权限后，需要校验当前账户访问是否有权限，整体思路如下。

用户在访问资源的时候，通过filter可以拦截到用户访问的所有的资源列表，然后针对当前访问的uri，取出用户所拥有的所有uri的list，分别进行校验。

相关部分代码如下：

```
//如果已经登录，比较权限
        else if (!hasAuth(account.getPermissions(),uri)){
            request.setAttribute("msg", "您无权访问当前页面:" + uri);
            request.getRequestDispatcher("/account/errorPage").forward(request, response);
            return;
        }
        //如果已经登录，则放行
        filterChain.doFilter(request, response);
```

如果账号已经登录，则进入校验，判断访问的当前的uri是不是在权限list里。

```
//判断是否有权限
    private boolean hasAuth(List<Permission> permissionList,String uri){
        System.out.println("permissionList的大小："+permissionList.size());
        for(Permission permission : permissionList){
            System.out.println("这里的权限值是："+permission.getUri());
            if(uri.startsWith(permission.getUri())){
                return true;
            }
        }
        return false;
    }
```

*备注*：为了后面测试方便，暂时屏蔽掉权限校验部分`com.qhc.oa.filter`相关代码

# 11.使用FastDFS上传文件

这里使用第三方的客户端

```
        <!-- FastDFS第三方客户端-->
        <dependency>
            <groupId>com.github.tobato</groupId>
            <artifactId>fastdfs-client</artifactId>
            <version>1.27.2</version>
        </dependency>
```

提交表单内容；

```
   <!--表单开始↓-->
    <form action="/account/fileUploadController" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td>角色：</td>
                <td>
                    <th:block th:each="item : ${session.account.roles}">
                        <a>[[${item.rolename}]]</a>
                    </th:block>
                </td>
            </tr>

            <tr>
                <td>登录名：</td>
                <td>[[${session.account.loginname}]]</td>
            </tr>

            <tr>
                <td>头像：</td>
                <td>
                    上传文件：<input type="file" name="filename"><br/>
                    <input type="submit"/>

                </td>
            </tr>

        </table>

    </form>
    <!--表单结束↑-->
```

**操作代码**

```
    //上传头像
    @RequestMapping("fileUploadController")
    public String fileUpload(MultipartFile filename,HttpServletRequest request){
        Set<MetaData> metaData = new HashSet<MetaData>();
        metaData.add(new MetaData("Author","quhaichuan"));
        metaData.add(new MetaData("CreateDate","2020-04-06"));
        try {
//            StorePath uploadFile = fastFileStorageClient.uploadFile(filename.getInputStream(),filename.getSize(),
//                    FilenameUtils.getExtension(filename.getOriginalFilename()),metaData);//            StorePath uploadFile = fastFileStorageClient.uploadFile(filename.getInputStream(),filename.getSize(),
            StorePath uploadFile = fastFileStorageClient.uploadImageAndCrtThumbImage(filename.getInputStream(),filename.getSize(),
                    FilenameUtils.getExtension(filename.getOriginalFilename()),metaData);
            System.out.println(uploadFile.getFullPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "account/profile";
    }
```

`fastFileStorageClient.uploadFile`上传普通文件

`fastFileStorageClient.uploadImageAndCrtThumbImage`上传图片并裁减缩略图，但是要在配置文件中配置参数



```
  thumb-image:
    width: 150
    height: 150
```

`org.apache.commons.io.FilenameUtils.getExtension`使用这个获取文件名后缀

**返回结果带group**

uploadFile.getFullPath() ： group1/M00/00/00/wKiWDV0u7ZKALKtNAAADP9sEx2w432.sql

**不带group**

uploadFile.getPath() ： M00/00/00/wKiWDV0u7ZKALKtNAAADP9sEx2w432.sql

图片上传后的结果

```
wKgBa16LOAqAPkUNAAXm4g90EwM593_150x150.png  #缩略图
wKgBa16LOAqAPkUNAAXm4g90EwM593.png	#原图
wKgBa16LOAqAPkUNAAXm4g90EwM593.png-m  #元数据

```



## 下载文件

```
@RequestMapping("/down")
	@ResponseBody
	public ResponseEntity<byte[]> down(HttpServletResponse resp) {
		
		DownloadByteArray cb = new DownloadByteArray();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentDispositionFormData("attachment", "aaa.png");
		byte[] bs = fc.downloadFile("group1", "M00/00/00/wKgBa16L-IeAVEV9AAGOnXelCpo695.png", cb);
		
	return new ResponseEntity<>(bs,headers,HttpStatus.OK);
	}
```

在浏览器中输入`http://localhost:8080/account/down`即可下载对应的图片，结果图片是命名好的。