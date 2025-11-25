package com.multi.y2k4.service.management;


import com.multi.y2k4.mapper.management.UserMapper;
import com.multi.y2k4.vo.user.UserVO;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserVO checkLogin(String id, String inputPassword) {
        // 1. ID로 유저 정보를 먼저 가져옴 (비밀번호 체크 없이)
        // UserMapper.xml에서 password 조건을 뺐으므로 id만 넘겨도 됩니다.
        UserVO user = userMapper.checkLogin(id, null);

        // 2. 유저가 없으면 null 반환
        if (user == null) {
            return null;
        }

        // 3. 비밀번호 비교 (입력받은값 vs DB에저장된값)
        // matches(평문, 암호화된값) 메서드가 내부적으로 확인해줌
        if (passwordEncoder.matches(inputPassword, user.getPassword())) {
            return user; // 일치하면 유저 정보 반환
        } else {
            return null; // 불일치하면 null 반환 (로그인 실패)
        }
    }

    public int addUser(UserVO userVO) {
        // 사용자가 입력한 비밀번호를 암호화
        String encodedPwd = passwordEncoder.encode(userVO.getPassword());
        userVO.setPassword(encodedPwd); // 암호화된 걸로 교체

        return userMapper.addUser(userVO);
    }

    public boolean existsById(String id) {
        return userMapper.existsById(id);
    }

    public UserVO selectById(String id) {
        return userMapper.selectById(id);
    }

    public int updateMypage(String id, String name, String email, String phone,
                            String currentPassword, String newPassword) {

        // 1. 비밀번호 변경 요청이 있을 때만 검증 수행
        if (newPassword != null && !newPassword.isBlank()) {

            // (1) DB에서 현재 회원 정보(암호화된 비번 포함)를 가져옴
            UserVO user = userMapper.selectById(id);

            // (2) 자바에서 비밀번호 비교 (matches: 평문 vs 암호화)
            // user.getPassword()는 DB에 저장된 암호화된 비밀번호입니다.
            if (user == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                return 0; // 비밀번호 불일치 시 업데이트 중단 (0 반환)
            }

            // (3) 새 비밀번호 암호화
            newPassword = passwordEncoder.encode(newPassword);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("name", name);
        params.put("email", email);
        params.put("phone", phone);

        // 암호화된 새 비밀번호 전달 (변경 없으면 null)
        params.put("newPassword", newPassword);

        // SQL 실행 (이제 ID만 맞으면 업데이트됨)
        return userMapper.updateMypage(params);
    }
}
