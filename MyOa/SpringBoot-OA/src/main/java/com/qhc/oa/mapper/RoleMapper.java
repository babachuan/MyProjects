package com.qhc.oa.mapper;

import com.qhc.oa.entity.Permission;
import com.qhc.oa.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RoleMapper继承基类
 */
@Repository
public interface RoleMapper extends MyBatisBaseDao<Role, Integer, RoleExample> {
    void addPermissions(int id, int[] permissions);

    List<Permission> selectByRolePermission(int id);
}