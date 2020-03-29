package com.qhc.oa.mapper;

import com.qhc.oa.entity.Account;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AccountMapper继承基类
 */
@Repository
public interface AccountMapper extends MyBatisBaseDao<Account, Integer, AccountExample> {

    List<Account> selectByPermission();



    List<Account> selectByByLoginNameAndPasswordAll(String loginName, String password);
//    Account selectByByLoginNameAndPasswordAll(String loginName, String password);
}