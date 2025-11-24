package com.multi.y2k4.service.management;


import com.multi.y2k4.mapper.management.UserMapper;
import com.multi.y2k4.vo.user.UserVO;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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

    public boolean existsById(String id){
        return  userMapper.existsById(id);
    }

    public UserVO selectById(String id){return userMapper.selectById(id);}

    public int updateMypage(String id,
                            String name,
                            String email,
                            String phone,
                            String currentPassword,
                            String newPassword) {

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);
        params.put("email", email);
        params.put("phone", phone);

        // 비밀번호 변경 안 하는 경우: null/빈 문자열로 넘김
        params.put("currentPassword", currentPassword);
        params.put("newPassword", newPassword);

        return userMapper.updateMypage(params);
    }
}
