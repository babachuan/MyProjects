package com.qhc.oa.controller;

import com.qhc.oa.RespState;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.service.PermissionService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager/permission")
public class ManagerRestController {

    @Autowired
    PermissionService permissionService;

    @RequestMapping("add")
    public RespState addOrUpdate(@RequestBody Permission permission){
        System.out.println("permission=="+ ToStringBuilder.reflectionToString(permission));
        permissionService.add(permission);

        return RespState.build(200);
    }
}
