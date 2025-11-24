package com.multi.y2k4.mapper.management;

import com.multi.y2k4.vo.user.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface UserMapper {

    UserVO checkLogin(@Param("id") String id, @Param("password") String password);

    int addUser(UserVO userVO);

    boolean existsById(@Param("id") String id);

    UserVO selectById(@Param("id") String id);

    int updateMypage(Map<String, Object> params);
}
