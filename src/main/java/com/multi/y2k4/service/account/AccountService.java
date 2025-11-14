package com.multi.y2k4.service.account;


import com.multi.y2k4.mapper.tenant.account.AccountMapper;
import com.multi.y2k4.vo.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;

    public List<Account> list_all(){return accountMapper.list_all();};
    public List<Account> list(Integer ac_id, String ac_name, String ac_loc){return accountMapper.list(ac_id,ac_name,ac_loc);};
    public Account searchById(int ac_id){return accountMapper.searchById(ac_id);} ;

    @Transactional
    public boolean addAccount(Account account) {
        try {
            int row = accountMapper.addAccount(account);
            return row == 1;
        } catch (DataIntegrityViolationException e) {
            // UNIQUE 제약조건 위반 등 DB 제약 위반 처리
            // 로그만 찍고 false 반환
            System.out.println("중복 키 오류: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean editAccount(Account account) {
        // 필요하면 여기서 account.getAc_id() null 체크 등 검증 로직 추가 가능
        try {
            int row = accountMapper.editAccount(account);
            return row == 1;
        } catch (DataIntegrityViolationException e) {
            // UNIQUE 제약조건 위반 등 DB 제약 위반 처리
            // 로그만 찍고 false 반환
            System.out.println("중복 키 오류: " + e.getMessage());
            return false;
        }
    }


    public int deleteAccount(int ac_id) {
        return accountMapper.deleteAccount(ac_id);
    }
}


