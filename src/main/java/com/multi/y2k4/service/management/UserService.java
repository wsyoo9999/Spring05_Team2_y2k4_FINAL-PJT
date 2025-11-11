package com.multi.y2k4.service.management;


import com.multi.y2k4.mapper.management.UserMapper;
import com.multi.y2k4.vo.user.UserVO;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public UserVO checkLogin(String id, String password){
        return  userMapper.checkLogin(id,password);
    }

    public int addUser(UserVO userVO){
        return  userMapper.addUser(userVO);
    }
}
