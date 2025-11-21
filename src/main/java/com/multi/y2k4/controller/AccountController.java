package com.multi.y2k4.controller;


import com.multi.y2k4.service.account.AccountService;
import com.multi.y2k4.vo.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/list")
    public List<Account> list_all(){
        return accountService.list_all();
    }

    @GetMapping("/searchById")
    public Account searchById(@RequestParam Integer ac_id){
        return accountService.searchById(ac_id);
    }
}
