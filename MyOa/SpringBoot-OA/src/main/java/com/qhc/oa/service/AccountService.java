package com.qhc.oa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qhc.oa.RespState;
import com.qhc.oa.entity.Account;
import com.qhc.oa.entity.Permission;
import com.qhc.oa.entity.Role;
import com.qhc.oa.mapper.AccountExample;
import com.qhc.oa.mapper.AccountMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        return accounts.size() == 0 ? null : accounts.get(0);
    }

    public List<Account> findAccountPermissions(){
        List<Account> alist = accountMapper.selectByPermission();
        return alist;
    }

    public PageInfo<Account> findByPage(int pageNum, int pageSize) {

        //测试关联查询
        /**
         * 这里打印得到的结果如下：
         * com.qhc.oa.entity.Account@5039bc50[id=<null>,loginname=sunwukong,password=<null>,nickname=孙悟空,age=100,location=中原,role=大师兄,roles=[Role [Hash = 31358035, id=1, rolename=管理员, serialVersionUID=1]],permissions=[Permission [Hash = -1532097293, id=1, pname=系统管理, uri=/system/manager, c=1, r=1, u=1, d=1, serialVersionUID=1]]]
         * com.qhc.oa.entity.Account@184a604a[id=<null>,loginname=sunwukong,password=<null>,nickname=孙悟空,age=100,location=中原,role=大师兄,roles=[Role [Hash = 649272465, id=2, rolename=前台用户, serialVersionUID=1]],permissions=[Permission [Hash = 1743035737, id=2, pname=实验管理, uri=/trial/management, c=1, r=1, u=1, d=1, serialVersionUID=1]]]
         */
        List<Account> alist = accountMapper.selectByPermission();
        for(Account slist : alist) {
            System.out.println(ToStringBuilder.reflectionToString(slist));
        }
        //启动分页
        PageHelper.startPage(pageNum, pageSize);
        //查询出所有数据，并通过PageInfo接收
        AccountExample example = new AccountExample();
        List<Account> accounts = accountMapper.selectByExample(example);
        //这里传的参数5是为了后面显示5页
        PageInfo<Account> pageInfo = new PageInfo<>(accounts, 5);
        return pageInfo;
    }

    public RespState deleteById(int id) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andIdEqualTo(id);
        int row = accountMapper.deleteByExample(accountExample);
        if (row == 1) {
            return RespState.build(200);
        } else {
            return RespState.build(500, "删除出错");
        }
    }


    public Account findByLoginNameAndPasswordAll(String loginName, String password) {
//        AccountExample accountExample = new AccountExample();
//        accountExample.createCriteria().andPasswordEqualTo(password).andLoginnameEqualTo(loginName);
//        List<Account> account = accountMapper.selectByExample(accountExample);
//        return account.get(0);

        List<Account> accountList = accountMapper.selectByByLoginNameAndPasswordAll(loginName,password);
//        Account accountList = accountMapper.selectByByLoginNameAndPasswordAll(loginName,password);
//        System.out.println("登录后获取到的账户："+ToStringBuilder.reflectionToString(accountList));
        List<Role> rList = new ArrayList<>();
        List<Permission> pList = new ArrayList<>();

        for(Account item : accountList){
            rList.add((item.getRoles().size() > 0?item.getRoles().get(0):null));
            pList.add((item.getPermissions().size() > 0 ? item.getPermissions().get(0):null));
            System.out.println("登录后获取到的账户："+ToStringBuilder.reflectionToString(item));
        }
        if(accountList.size() > 0){
        Account finalAccount = accountList.get(0);
        finalAccount.setRoles(rList);
        finalAccount.setPermissions(pList);
        return finalAccount;}else {
            return null;

        }
    }
}
