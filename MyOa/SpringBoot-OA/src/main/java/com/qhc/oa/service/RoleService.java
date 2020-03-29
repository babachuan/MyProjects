package com.qhc.oa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.entity.Role;
import com.qhc.oa.mapper.RoleExample;
import com.qhc.oa.mapper.RoleMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleMapper roleMapper;

    public PageInfo<Role> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        RoleExample roleExample = new RoleExample();
        List<Role> roles = roleMapper.selectByExample(roleExample);
        System.out.println(ToStringBuilder.reflectionToString("权限列表："+roles));
        return new PageInfo<>(roles);

    }

    public void addRole(Role role) {
        roleMapper.insert(role);
    }

    public Role findById(int id) {
        Role role = roleMapper.selectByPrimaryKey(id);
        return role;
    }

    public void updateRole(Role role) {
        roleMapper.updateByPrimaryKeySelective(role);
    }

    public void addPermission(int id, int[] permissions) {
        roleMapper.addPermissions(id,permissions);
    }

    public List<Permission> getRolePermission(int id) {
        List<Permission> rpList = roleMapper.selectByRolePermission(id);
        return rpList;
    }
}
