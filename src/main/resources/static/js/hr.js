// hr.js
const API_BASE_URL = '/api/hr';

// ================================================================
// 유틸리티 함수
// ================================================================

// 날짜 포맷 (YYYY-MM-DD)
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '-'; // ★ Invalid Date 방어
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 시간 포맷 (HH:MM)
function formatTime(dateTimeString) {
    if (!dateTimeString) return '-';
    const parts = String(dateTimeString).split('T'); // ★ 방어적 캐스팅
    const time = parts.length > 1 ? parts[1] : null;
    if (time) return time.substring(0, 5);
    return '-';
}

// 숫자 천단위 콤마 함수
function numberFormat(num) {
    if (num === null || num === undefined || num === '') return '0';
    const n = Number(num);                 // ★ parseInt → Number
    if (Number.isNaN(n)) return '0';       // ★ NaN 방어
    return n.toLocaleString('ko-KR');
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
    // 1. 테이블 상단 액션 버튼 HTML
    // ⚠️ 기존 data-file / data-fn을 그대로 둡니다(기능 유지):contentReference[oaicite:4]{index=4}
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addSale">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;

    // 2. 실제 데이터와 테이블 HTML
    const tableHtml = await employees_fetch_data({});

    // 3. 합쳐서 반환
    return actionRow + tableHtml;
}

// ★ 불필요 await 제거(동작 동일)
export function employees_list(formData) {
    return employees_fetch_data(formData);
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
                            <td>
                                <strong 
                                    data-action="detail" 
                                    data-file="hr" 
                                    data-fn="employee_detail_popup"
                                    data-emp-id="${row.emp_id}" 
                                    style="cursor: pointer; color: #007bff; text-decoration: underline;">
                                    ${row.emp_name || ''}
                                </strong>
                            </td>
                            <td>${row.dept_name || ''}</td>
                            <td>${row.position || ''}</td>
                            <td>${formatDate(row.hire_date) || ''}</td>
                            <td>${row.status || ''}</td>
                            <td>${row.phone_number || ''}</td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="7" style="text-align:center;">데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;
    } catch (err) {
        console.error("[HR] employees_list 로딩 실패:", err); // ★ 로그 prefix 통일
        return table + `<tbody><tr><td colspan="7" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}

// 직원 상세 조회 및 수정 팝업 함수
export function employee_detail_popup(e) {
    const empId = e.dataset.empId;
    if (!empId) {
        console.error("[HR] Employee ID가 없습니다."); // ★ 로그 prefix
        return;
    }
    const url = `./popup/employeeDetail.html?empId=${empId}`;
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

export function attendance_list(formData) {
    return attendance_fetch_data(formData);
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

                tbody += `<tr 
                            data-action="detail" 
                            data-file="hr" 
                            data-fn="attendance_detail_popup"
                            data-attendance-id="${row.attendance_id}"
                            style="cursor: pointer;">
                            <td>${formatDate(row.work_date) || '-'}</td>
                            <td>${row.emp_id || '-'}</td>
                            <td>${row.emp_name || '-'}</td>
                            <td>${check_in_time}</td>
                            <td>${check_out_time}</td>
                            <td class="${status_class}"><strong>${row.attendance_status || '-'}</strong></td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="6" style="text-align:center;">데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        console.error("[HR] attendance_list 로딩 실패:", err); // ★ 로그 prefix
        return table + `<tbody><tr><td colspan="6" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}

// 근태 기록 수정 팝업 함수
export function attendance_detail_popup(e) {
    const attendanceId = e.dataset.attendanceId;
    if (!attendanceId) {
        console.error("[HR] Attendance ID가 없습니다."); // ★ 로그 prefix
        return;
    }
    const url = `./popup/attendanceEdit.html?attendanceId=${attendanceId}`;
    const features = 'width=500,height=400,resizable=yes,scrollbars=yes';
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

export function salary_list(formData) {
    return salary_fetch_data(formData);
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
                            <td class="num">
                                <strong 
                                    data-action="detail" 
                                    data-file="hr" 
                                    data-fn="salary_detail_popup"
                                    data-salary-id="${row.salary_id}" 
                                    style="cursor: pointer; color: blue; text-decoration: underline;">
                                    ${numberFormat(row.total_pay)}
                                </strong>
                            </td>
                            <td>${row.bank_name || '-'}</td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="9" style="text-align:center;">데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        console.error("[HR] salary_list 로딩 실패:", err); // ★ 로그 prefix 및 메시지 정리
        return table + `<tbody><tr><td colspan="9" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}

export function salary_detail_popup(e) {
    const salaryId = e.dataset.salaryId;
    if (!salaryId) {
        console.error("[HR] Salary ID가 없습니다."); // ★ 로그 prefix
        return;
    }
    const url = `./popup/salaryDetail.html?salaryId=${salaryId}`;
    const features = 'width=500,height=500,resizable=yes,scrollbars=yes';
    window.open(url, `salary_detail_${salaryId}`, features);
}
