package com.qhc.oa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.mapper.PermissionExample;
import com.qhc.oa.mapper.PermissionMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    public List<Permission> findAll() {
        PermissionExample permissionExample = new PermissionExample();
        List<Permission> permissionList = permissionMapper.selectByExample(permissionExample);
        return permissionList;
    }

    public PageInfo<Permission> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PermissionExample example =  new PermissionExample();
        List<Permission> permissions = permissionMapper.selectByExample(example);
        PageInfo<Permission> pageInfo = new PageInfo<>(permissions,5);
        return pageInfo;
    }

    public Permission findById(int id) {
        Permission permission = permissionMapper.selectByPrimaryKey(id);
        return permission;
    }

    public void update(Permission permission) {
        System.out.println("permission的值=="+ ToStringBuilder.reflectionToString(permission));
        permissionMapper.updateByPrimaryKeySelective(permission);
    }

    public void add(Permission permission) {
        System.out.println("permission的值=="+ ToStringBuilder.reflectionToString(permission));
        permissionMapper.insert(permission);
    }
}
