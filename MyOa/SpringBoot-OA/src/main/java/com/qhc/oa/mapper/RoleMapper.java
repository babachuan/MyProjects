package com.qhc.oa.mapper;

import com.qhc.oa.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * RoleMapper继承基类
 */
@Repository
public interface RoleMapper extends MyBatisBaseDao<Role, Integer, RoleExample> {
    void addPermissions(int id, int[] permissions);
}