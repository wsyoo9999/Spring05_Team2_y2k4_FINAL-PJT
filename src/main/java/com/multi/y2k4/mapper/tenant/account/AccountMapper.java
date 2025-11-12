package com.multi.y2k4.mapper.tenant.account;


import com.multi.y2k4.vo.account.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {
    public List<Account> list_all();
    public Account searchById(@Param("ac_id")int id);
}
