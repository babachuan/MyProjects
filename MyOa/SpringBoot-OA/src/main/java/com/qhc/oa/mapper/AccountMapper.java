package com.qhc.oa.mapper;

import com.qhc.oa.entity.Account;
import com.qhc.oa.entity.AccountExample;
import org.springframework.stereotype.Repository;

/**
 * AccountMapper继承基类
 */
@Repository
public interface AccountMapper extends MyBatisBaseDao<Account, Integer, AccountExample> {
}