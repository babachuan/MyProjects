package com.qhc.oa.mapper;

import com.qhc.oa.entity.Permission;
import org.springframework.stereotype.Repository;

/**
 * PermissionMapper继承基类
 */
@Repository
public interface PermissionMapper extends MyBatisBaseDao<Permission, Integer, PermissionExample> {
}