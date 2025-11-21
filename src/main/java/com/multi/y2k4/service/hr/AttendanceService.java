package com.multi.y2k4.service.hr;

import com.multi.y2k4.mapper.tenant.hr.AttendanceMapper;
import com.multi.y2k4.vo.hr.Attendance;
import com.multi.y2k4.mapper.tenant.hr.EmployeeMapper;
import com.multi.y2k4.vo.hr.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceMapper attendanceMapper;
    private final EmployeeMapper employeeMapper;

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
    public boolean generateDailyAttendance() {
        // 1. 모든 '재직'중인 직원 목록을 가져온다. (정렬/검색 조건 없이)
        List<Employee> allEmployees = employeeMapper.getEmployeeList(null, null, null, null);

        if (allEmployees == null || allEmployees.isEmpty()) {
            return true; // 직원이 없으면 할 일이 없으므로 성공 처리
        }

        // 2. 오늘 날짜 설정
        LocalDate today = LocalDate.now();

        // 3. 일괄 INSERT를 위한 List 생성
        List<Attendance> attendanceList = new ArrayList<>();

        for (Employee emp : allEmployees) {
            // '재직' 상태인 직원만 대상으로 함
            if ("재직".equals(emp.getStatus())) {
                Attendance att = new Attendance();
                att.setEmp_id(emp.getEmp_id());
                att.setWork_date(today); // 오늘 날짜
                att.setCheck_in(today.atTime(9, 0));  // 오늘 09:00
                att.setCheck_out(today.atTime(18, 0)); // 오늘 18:00
                att.setAttendance_status("정상"); // 기본값 '정상'

                attendanceList.add(att);
            }
        }

        // 4. 리스트가 비어있지 않으면 Mapper를 통해 일괄 INSERT
        if (!attendanceList.isEmpty()) {
            int insertedRows = attendanceMapper.addBulkAttendance(attendanceList);
            return insertedRows > 0;
        }

        return true;
    }

    public int addBulkAttendance(List<Attendance> attendanceList) {
        return attendanceMapper.addBulkAttendance(attendanceList);
    }
    public void applyVacation(Integer empId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> vacationList = new ArrayList<>();

        // 시작일부터 종료일까지 하루씩 반복
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Attendance att = new Attendance();
            att.setEmp_id(empId);
            att.setWork_date(date);
            att.setCheck_in(date.atTime(9, 0));
            att.setCheck_out(date.atTime(18, 0));
            att.setAttendance_status("휴가");

            vacationList.add(att);
        }

        if (!vacationList.isEmpty()) {
            attendanceMapper.addBulkAttendance(vacationList);
        }
    }
}