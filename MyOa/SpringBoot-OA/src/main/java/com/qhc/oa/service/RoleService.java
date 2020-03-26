package com.qhc.oa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qhc.oa.entity.Role;
import com.qhc.oa.mapper.RoleExample;
import com.qhc.oa.mapper.RoleMapper;
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
}
