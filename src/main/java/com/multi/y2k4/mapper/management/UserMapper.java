package com.multi.y2k4.mapper.management;

import com.multi.y2k4.vo.user.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    UserVO checkLogin(@Param("id") String id, @Param("password") String password);

    int addUser(UserVO userVO);

}
