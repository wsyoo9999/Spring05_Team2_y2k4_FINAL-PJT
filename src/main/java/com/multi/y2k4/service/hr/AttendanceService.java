package com.multi.y2k4.service.hr;

import com.multi.y2k4.mapper.tenant.hr.AttendanceMapper;
import com.multi.y2k4.vo.hr.Attendance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;

    public List<Attendance> getAttendanceList(String search_keyword, LocalDate start_date, LocalDate end_date) {
        return attendanceMapper.getAttendanceList(search_keyword, start_date, end_date);
    }

    public Attendance getAttendanceDetail(Integer attendanceId) {
        return attendanceMapper.getAttendanceDetail(attendanceId);
    }

    /**
     * @return 수정 성공 시 true
     */
    public boolean updateAttendanceStatus(Attendance attendance) {
        // update 쿼리의 반환 값(영향받은 행 수)이 0보다 크면 true
        return attendanceMapper.updateAttendanceStatus(attendance) > 0;
    }
}