package com.multi.y2k4.service.db;

import com.multi.y2k4.mapper.tenant.db.DatabaseMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DBService {
    private final DatabaseMapper databaseMapper;

    public int existsDatabase(String dbName){
        return databaseMapper.existsDatabase(dbName);
    }
    public void createDatabase(String dbName){
        databaseMapper.createDatabase(dbName);
    }
}
