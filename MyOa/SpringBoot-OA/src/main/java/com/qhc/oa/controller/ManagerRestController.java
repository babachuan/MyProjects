package com.qhc.oa.controller;

import com.qhc.oa.RespState;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.entity.Role;
import com.qhc.oa.service.PermissionService;
import com.qhc.oa.service.RoleService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager")
public class ManagerRestController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleService roleService;

    @RequestMapping("/permission/update")
    public RespState addOrUpdate(@RequestBody Permission permission){
        permissionService.update(permission);

        return RespState.build(200);
    }

    @RequestMapping("/permission/add")
    public RespState add(@RequestBody Permission permission){

        permissionService.add(permission);

        return RespState.build(200);
    }


    //添加角色
    @RequestMapping("role/add")
    public RespState roleAdd(@RequestBody Role role){
        System.out.println("role=="+ ToStringBuilder.reflectionToString(role));
        roleService.addRole(role);
        return RespState.build(200);
    }

    //修改角色
    @RequestMapping("role/update")
    public RespState roleUpdate(@RequestBody Role role){
        System.out.println("role=="+ ToStringBuilder.reflectionToString(role));
        roleService.updateRole(role);
        return RespState.build(200);
    }
}
