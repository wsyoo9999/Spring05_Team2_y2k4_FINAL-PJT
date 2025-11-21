package com.multi.y2k4.controller;


import com.multi.y2k4.service.account.AccountService;
import com.multi.y2k4.vo.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/list")
    public List<Account> accountList(   @RequestParam(required = false) Integer ac_id,
                                        @RequestParam(required = false) String ac_name,
                                        @RequestParam(required = false) String ac_loc){
        return accountService.list(ac_id,ac_name,ac_loc);
    }

    @GetMapping("/searchById")
    public Account searchById(@RequestParam Integer ac_id){
        return accountService.searchById(ac_id);
    }

    @PostMapping("/add")

    public boolean addAccount(@RequestParam(required = false) String ac_name,
                              @RequestParam(required = false) String ac_manager,
                              @RequestParam(required = false) String ac_loc,
                              @RequestParam(required = false) String ac_phone,
                              @RequestParam(required = false) String ac_email){
        Account ac = new Account();
        ac.setAc_name(ac_name);
        ac.setAc_manager(ac_manager);
        ac.setAc_loc(ac_loc);
        ac.setAc_phone(ac_phone);
        ac.setAc_email(ac_email);

        return  accountService.addAccount(ac);

    }

    @PostMapping("/edit")
    public boolean edit(    @RequestParam Integer ac_id,
                            @RequestParam(required = false) String ac_name,
                            @RequestParam(required = false) String ac_manager,
                            @RequestParam(required = false) String ac_loc,
                            @RequestParam(required = false) String ac_phone,
                            @RequestParam(required = false) String ac_email){
        Account ac = new Account();
        ac.setAc_id(ac_id);
        ac.setAc_name(ac_name);
        ac.setAc_manager(ac_manager);
        ac.setAc_loc(ac_loc);
        ac.setAc_phone(ac_phone);
        ac.setAc_email(ac_email);

        return accountService.editAccount(ac);
    }

    @PostMapping("/delete")
    public boolean delete(    @RequestParam(required = false) Integer ac_id){
        //추후 삭제를 구현할때, ac_id를 FK로 쓰는 테이블이 여럿 있으므로 해당 테이블에서 지울 ac_id를 사용하는 데이터들이 있는지 확인하는 과정이 필수
        //만약 해당 ac_id를 FK로 쓰고 있는 데이터가 단 하나라도 없을 경우에만 지우는 것을 허용
//        accountService.deleteAccount(ac_id);
        return true;
    }

}
