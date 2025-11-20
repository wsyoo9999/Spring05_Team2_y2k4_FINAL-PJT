package com.multi.y2k4.vo.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserVO {
    private int user_id;
    private String id;
    private String password;
    private String name;
    private String email;
    private String company_id;
    private LocalDate birthday;
    private String phone;
}
