package com.multi.y2k4.mapper.tenant.account;


import com.multi.y2k4.vo.account.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    public List<Account> list_all();
    public List<Account> list(@Param("ac_id") Integer ac_id, @Param("ac_name")String ac_name, @Param("ac_loc") String ac_loc);
    public int editAccount(Account account);
    public int addAccount(Account account);
    public int deleteAccount(@Param("ac_id") Integer ac_id);
    public Account searchById(@Param("ac_id")int id);
}
