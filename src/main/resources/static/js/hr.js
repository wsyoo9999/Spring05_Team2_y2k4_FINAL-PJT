// hr.js
const API_BASE_URL = '/api/hr';

// ================================================================
// 유틸리티 함수
// ================================================================

// 날짜 포맷 (YYYY-MM-DD)
function formatDate(dateString) {
    if (!dateString) return '-';
    // String(dateString)이 없으므로, 이미 객체일 경우를 대비해 new Date 처리
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 시간 포맷 (HH:MM)
function formatTime(dateTimeString) {
    if (!dateTimeString) return '-';
    // LocalDateTime 문자열은 'YYYY-MM-DDTHH:MM:SS' 형식이므로 T를 기준으로 분리
    const parts = dateTimeString.split('T');
    const time = parts.length > 1 ? parts[1] : null;

    if (time) return time.substring(0, 5);
    return '-';
}

// 숫자 천단위 콤마 함수 추가
function numberFormat(num) {
    if (num === null || num === undefined) return '0';
    return parseInt(num).toLocaleString('ko-KR');
}


// ================================================================
// 1. 직원 목록 (Employees)
// ================================================================

export function employees_search_form() {
    const search_bar = `<form data-file="hr" data-fn="employees_list">
                            <label>정렬:
                                <select name="sort">
                                    <option value="emp_id,asc">사번↑</option>
                                    <option value="emp_id,desc">사번↓</option>
                                    <option value="emp_name,asc">이름↑</option>
                                </select>
                            </label>
                            
                            <label>이름:
                                <input type="text" name="search_name" placeholder="이름 검색" />
                            </label>
                            <label>부서:
                                <input type="text" name="search_dept" placeholder="부서 검색" />
                            </label>
                            <label>직급:
                                <input type="text" name="search_position" placeholder="직급 검색" />
                            </label>
                            
                            <button type="submit" data-action="search" class="search_btn">검색</button>
                        </form>`;
    return search_bar;
}

export async function employees_listAll() {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addSale">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    // actionRow에 테이블 HTML을 덧붙여 반환해야 합니다.
    const employeeTableHtml = await employees_fetch_data({}); // 🌟 이 부분이 실행되어야 함
    return actionRow + employeeTableHtml;
}

export async function employees_list(formData) {
    return await employees_fetch_data(formData);
}

async function employees_fetch_data(formData) {
    const sort = formData.sort || 'emp_id,asc';
    const search_name = formData.search_name || '';
    const search_dept = formData.search_dept || '';
    const search_position = formData.search_position || '';

    let table = `<table>
                    <thead>
                        <tr>
                            <th>사번</th>
                            <th>이름</th>
                            <th>부서</th>
                            <th>직급</th>
                            <th>입사일</th>
                            <th>재직상태</th>
                            <th>연락처</th>
                            <th>관리</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/employees`,
            method: 'GET',
            dataType: 'json',
            data: {
                sort: sort,
                search_name: search_name,
                search_dept: search_dept,
                search_position: search_position
            }
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                tbody += `<tr>
                <td>${row.emp_id || ''}</td>
                <td>${row.emp_name || ''}</td>
                <td>${row.dept_name || ''}</td>
                <td>${row.position || ''}</td>
                <td>${formatDate(row.hire_date) || ''}</td>
                <td>${row.status || ''}</td>
                <td>${row.phone_number || ''}</td>
                <td class="actions">
                    <button 
                        data-action="detail"
                        data-file="hr"
                        data-fn="employee_detail_popup"
                        data-emp-id="${row.emp_id}">
                        <i class="fas fa-edit"></i>
                    </button>
                </td>
              </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="7" style="text-align:center;">데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;
    } catch (err) {
        console.error("employees_list 로딩 실패:", err);
        return table + `<tbody><tr><td colspan="7" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}

// 직원 상세 조회 및 수정 팝업 함수
export function employee_detail_popup(e) {
    // 팝업 URL에 전달할 사번(empId) 가져오기
    const empId = e.dataset.empId;
    if (!empId) {
        console.error("Employee ID가 없습니다.");
        return;
    }

    const url = `./popup/employeeDetail.html?empId=${empId}`; // ID를 쿼리 파라미터로 전달
    const features = 'width=600,height=450,resizable=yes,scrollbars=yes';
    window.open(url, `employee_detail_${empId}`, features);
}


// ================================================================
// 근태 현황 (Attendance)
// ================================================================

export function attendance_search_form() {
    const today = new Date().toISOString().substring(0, 10);

    return `<form data-file="hr" data-fn="attendance_list">
                <label>시작일:
                    <input type="date" name="start_date" value="" />
                </label>
                <label>종료일:
                    <input type="date" name="end_date" value="${today}" />
                </label>
                <label>검색:
                    <input type="text" name="search_keyword" placeholder="이름/상태 검색" />
                </label>
                <button type="submit" data-action="search" class="search_btn">검색</button>
            </form>`;
}

export async function attendance_listAll() {
    const today = new Date().toISOString().substring(0, 10);
    const formData = { end_date: today };
    return await attendance_fetch_data(formData);
}

export async function attendance_list(formData) {
    return await attendance_fetch_data(formData);
}

async function attendance_fetch_data(formData) {
    let table = `<table>
                    <thead>
                        <tr>
                            <th>근무일자</th>
                            <th>사번</th>
                            <th>이름</th>
                            <th>출근 시간</th>
                            <th>퇴근 시간</th>
                            <th>근무 상태</th>
                            <th>관리</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    const start_date = formData.start_date || '';
    const end_date = formData.end_date || '';

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/attendance`,
            method: 'GET',
            dataType: 'json',
            data: {
                start_date: start_date,
                end_date: end_date,
                search_keyword: formData.search_keyword
            }
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                const check_in_time = formatTime(row.check_in);
                const check_out_time = formatTime(row.check_out);

                let status_class = '';
                if (row.attendance_status === '지각' || row.attendance_status === '결근') {
                    status_class = 'status-alert';
                }

                // 🚨 data-action="detail"로 통일
                tbody += `<tr>
                            <td>${formatDate(row.work_date) || '-'}</td>
                            <td>${row.emp_id || '-'}</td>
                            <td>${row.emp_name || '-'}</td>
                            <td>${check_in_time}</td>
                            <td>${check_out_time}</td>
                            <td class="${status_class}"><strong>${row.attendance_status || '-'}</strong></td>
                            <td class="actions">                    
                                <button 
                                    data-action="detail" 
                                    data-file="hr" 
                                    data-fn="attendance_detail_popup"
                                    data-attendance-id="${row.attendance_id}" >
                                    <i class="fas fa-edit"></i>
                                </button>
                            </td>
                          </tr>`; // 🚩 초과 근무 필드 제거
            });
        } else {
            tbody += '<tr><td colspan="6" style="text-align:center;">데이터가 없습니다.</td></tr>'; // 🚩 colspan 7 -> 6
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        console.error("attendance_list 로딩 실패:", err);
        return table + `<tbody><tr><td colspan="6" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`; // 🚩 colspan 7 -> 6
    }
}

// 근태 기록 수정 팝업 함수
export function attendance_detail_popup(e) {
    const attendanceId = e.dataset.attendanceId;
    if (!attendanceId) {
        console.error("Attendance ID가 없습니다.");
        return;
    }

    // URL은 그대로 attendanceEdit.html을 사용 (수정 기능이 목적이므로)
    const url = `./popup/attendanceEdit.html?attendanceId=${attendanceId}`;
    const features = 'width=500,height=500,resizable=yes,scrollbars=yes';
    window.open(url, `attendance_detail_${attendanceId}`, features);
}


// ================================================================
// 3. 급여 대장 (Salary)
// ================================================================

export function salary_search_form() {
    const currentYear = new Date().getFullYear();
    const months = [
        { value: '', label: '전체 월' },
        { value: 1, label: '1월' },
        { value: 2, label: '2월' },
        { value: 3, label: '3월' },
        { value: 4, label: '4월' },
        { value: 5, label: '5월' },
        { value: 6, label: '6월' },
        { value: 7, label: '7월' },
        { value: 8, label: '8월' },
        { value: 9, label: '9월' },
        { value: 10, label: '10월' },
        { value: 11, label: '11월' },
        { value: 12, label: '12월' }
    ];

    const monthOptions = months.map(m => `<option value="${m.value}">${m.label}</option>`).join('');

    return `<form data-file="hr" data-fn="salary_list">
                <label>년도 검색:
                    <input type="number" name="search_year" value="${currentYear}" placeholder="년도 입력 (예: 2025)" />
                </label>
                <label>월 선택:
                    <select name="search_month">
                        ${monthOptions}
                    </select>
                </label>
                <button type="submit" data-action="search" class="search_btn">검색</button>
            </form>`;
}

export async function salary_listAll() {
    const currentYear = new Date().getFullYear();
    const formData = { search_year: currentYear, search_month: '' };
    return await salary_fetch_data(formData);
}

export async function salary_list(formData) {
    return await salary_fetch_data(formData);
}

async function salary_fetch_data(formData) {
    const search_year = formData.search_year || null;
    const search_month = formData.search_month || null;

    let table = `<table>
                    <thead>
                        <tr>
                            <th>지급일자</th>
                            <th>사번</th>
                            <th>이름</th>
                            <th>기본급</th>
                            <th>수당</th>
                            <th>총 지급액</th>
                            <th>총 공제액</th>
                            <th>실수령액</th>
                            <th>은행명</th>
                            <th>상세보기</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/salary`,
            method: 'GET',
            dataType: 'json',
            data: { search_year: search_year, search_month: search_month }
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {

                tbody += `<tr>
                            <td>${formatDate(row.payment_date) || '-'}</td>
                            <td>${row.emp_id || '-'}</td>
                            <td>${row.emp_name || '-'}</td>
                            <td class="num">${numberFormat(row.basic_salary)}</td>
                            <td class="num">${numberFormat(row.allowance)}</td>
                            <td class="num"><strong>${numberFormat(row.total_gross)}</strong></td>
                            <td class="num" style="color: red;">${numberFormat(row.deduction_amount)}</td>
                            <td class="num"><stron>${numberFormat(row.total_pay)} </stron></td>
                            <td>${row.bank_name || '-'}</td>
                            <td class="actions">                    
                                <button 
                                    data-action="detail" 
                                    data-file="hr" 
                                    data-fn="salary_detail_popup"
                                    data-salary-id="${row.salary_id}" >
                                    <i class="fas fa-info-circle"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="9" style="text-align:center;">데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        // 🚨 에러 발생 시 로그 출력 후 빈 테이블 반환
        console.error("salary_list 로딩 실패 (numberFormat 함수 정의 누락):", err);
        return table + `<tbody><tr><td colspan="9" style="text-align:center; color:red;">데이터 로딩 실패 (스크립트 오류)</td></tr></tbody></table>`;
    }
}

export function salary_detail_popup(e) {
    const salaryId = e.dataset.salaryId;
    if (!salaryId) {
        console.error("Salary ID가 없습니다.");
        return;
    }

    const url = `./popup/salaryDetail.html?salaryId=${salaryId}`;
    const features = 'width=500,height=635,resizable=yes,scrollbars=yes';
    window.open(url, `salary_detail_${salaryId}`, features);
}
