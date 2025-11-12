package com.multi.y2k4.service.account;


import com.multi.y2k4.mapper.tenant.account.AccountMapper;
import com.multi.y2k4.vo.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;

    public List<Account> list_all(){return accountMapper.list_all();};
    public Account searchById(int ac_id){return accountMapper.searchById(ac_id);} ;
}
