package com.multi.y2k4.mapper.tenant.hr;

import com.multi.y2k4.vo.hr.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AttendanceMapper {

    /**
     * 근태 목록 조회 (검색 및 정렬 기능 포함)
     * @param search_keyword 이름 또는 상태 검색어
     * @param start_date 검색 시작일
     * @param end_date 검색 종료일
     * @return 근태 목록
     */
    List<Attendance> getAttendanceList(
            @Param("search_keyword") String search_keyword,
            @Param("start_date") LocalDate start_date,
            @Param("end_date") LocalDate end_date
    );

    /**
     * 근태 상세 조회
     */
    Attendance getAttendanceDetail(@Param("attendanceId") Integer attendanceId);

    /**
     * 근태 상태 수정
     */
    int updateAttendanceStatus(Attendance attendance);

    /**
     * [추가] 근태 기록 일괄 등록
     */
    int addBulkAttendance(@Param("attendanceList") List<Attendance> attendanceList);
}