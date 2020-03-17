package com.qhc.oa.service;

import com.qhc.oa.entity.Account;
import com.qhc.oa.mapper.AccountExample;
import com.qhc.oa.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AccountService {
    @Autowired
    AccountMapper accountMapper;

    public Account getAccountById(Integer id) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andIdEqualTo(id);
        Account accounts = accountMapper.selectByPrimaryKey(id);
        return accounts;
    }

    public Account findByLoginNameAndPassword(String loginName, String password) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria()
                .andLoginnameEqualTo(loginName)
                .andPasswordEqualTo(password);
        List<Account> accounts = accountMapper.selectByExample(accountExample);
        return accounts.size()==0?null:accounts.get(0);
    }
}
