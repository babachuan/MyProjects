package com.qhc.oa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qhc.oa.RespState;
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

    public PageInfo<Account> findByPage(int pageNum, int pageSize) {
        //启动分页
        PageHelper.startPage(pageNum,pageSize);
        //查询出所有数据，并通过PageInfo接收
        AccountExample example = new AccountExample();
        List<Account> accounts = accountMapper.selectByExample(example);
        //这里传的参数5是为了后面显示5页
        PageInfo<Account> pageInfo = new PageInfo<>(accounts,5);
        return  pageInfo;
    }

    public RespState deleteById(int id) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andIdEqualTo(id);
        int row = accountMapper.deleteByExample(accountExample);
        if(row == 1){
            return RespState.build(200);
        }else{
            return RespState.build(500,"删除出错");
        }
    }
}
