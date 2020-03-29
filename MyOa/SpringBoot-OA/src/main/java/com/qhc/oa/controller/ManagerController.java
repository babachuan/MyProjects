package com.qhc.oa.controller;

import com.github.pagehelper.PageInfo;
import com.qhc.oa.entity.Account;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.entity.Role;
import com.qhc.oa.mapper.RoleMapper;
import com.qhc.oa.service.AccountService;
import com.qhc.oa.service.PermissionService;
import com.qhc.oa.service.RoleService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;


    @Autowired
    AccountService accountService;

    @RequestMapping("permissionList")
    public String permissionList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
        PageInfo<Permission> page = permissionService.findByPage(pageNum, pageSize);
        System.out.println(ToStringBuilder.reflectionToString(page));
        model.addAttribute("permissionList", page);
        return "manager/permissionList";
    }

    //    修改权限
    @RequestMapping("/permissionModify")
    public String permissionModify(@RequestParam(value = "id") int id, Model model) {

        Permission permission = permissionService.findById(id);
        //调试代码
        System.out.println(ToStringBuilder.reflectionToString(permission));
        model.addAttribute("p", permission);

        return "manager/permissionModify";

    }

    //添加权限
    @RequestMapping("permissionAdd")
    public String permissionAdd() {
        return "manager/permissionAdd";
    }

    //角色管理列表
    @RequestMapping("roleList")
    public String roleList(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "5") int pageSize, Model model) {
        PageInfo<Role> page = roleService.findByPage(pageNum,pageSize);
        model.addAttribute("roleList",page);
        return "manager/roleList";
    }

    //添加角色
    @RequestMapping("roleAdd")
    public String roleAdd(){
        return "manager/roleAdd";
    }

    @RequestMapping("roleModify")
    public String roleModify(@RequestParam int id,Model model){
        Role role = roleService.findById(id);
        model.addAttribute("role",role);
        return "manager/roleModify";

    }


    //关联角色
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
}
