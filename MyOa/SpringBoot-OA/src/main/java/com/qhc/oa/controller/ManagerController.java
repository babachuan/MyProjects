package com.qhc.oa.controller;

import com.github.pagehelper.PageInfo;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("manager")
public class ManagerController {

    @Autowired
    PermissionService permissionService;

    @RequestMapping("permissionList")
    public String permissionList(@RequestParam(defaultValue="1") int pageNum, @RequestParam(defaultValue="5") int pageSize, Model model){
        PageInfo<Permission> page = permissionService.findByPage(pageNum,pageSize);
        model.addAttribute("permissionList",page);
        return "/manager/permissionList";
    }
}
