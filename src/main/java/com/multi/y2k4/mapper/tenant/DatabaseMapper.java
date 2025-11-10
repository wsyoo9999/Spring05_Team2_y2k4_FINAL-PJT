package com.multi.y2k4.mapper.tenant;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DatabaseMapper {

    int existsDatabase(@Param("dbName") String dbName);
    void createDatabase(@Param("dbName") String dbName);
}
