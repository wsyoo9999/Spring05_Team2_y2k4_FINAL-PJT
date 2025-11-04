// hr.js
const API_BASE_URL = '/api/hr';

// ================================================================
// 1. 직원 목록 (Employees)
// ================================================================


// 직원 목록 검색 폼
export function employees_search_form() {
    const search_bar = `<form data-file="hr" data-fn="employees_list">
                            <label>정렬:
                                <select name="sort">
                                    <option value="emp_id,asc">사번↑</option>
                                    <option value="emp_id,desc">사번↓</option>
                                    <option value="emp_name,asc">이름↑</option>
                                </select>
                            </label>
                            <label>검색:
                                <input type="text" name="search_keyword" placeholder="이름/부서/직급 검색" />
                            </label>
                            <button type="submit" data-action="search" class="search_btn">검색</button>
                        </form>`;
    return search_bar;
}

// 직원 목록 전체 조회
export async function employees_listAll() {
    return await employees_fetch_data({});
}

// 직원 목록 조건 검색
export async function employees_list(formData) {
    return await employees_fetch_data(formData);
}

// 직원 데이터 AJAX 호출 및 HTML 생성 공통 함수
async function employees_fetch_data(formData) {
    const sort = formData.sort || 'emp_id,asc';
    const search_keyword = formData.search_keyword || '';

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
            url: `${API_BASE_URL}/employees`, // HRController 호출
            method: 'GET',
            dataType: 'json',
            data: {
                sort: sort,
                search_keyword: search_keyword
            }
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                tbody += `<tr>
                            <td>${row.emp_id || ''}</td>
                            <td>${row.emp_name || ''}</td>
                            <td>${row.dept_name || ''}</td>
                            <td>${row.position || ''}</td>
                            <td>${row.hire_date || ''}</td>
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
        // [테스트용]: 실제 API 연결이 끊긴 경우 더미 데이터로 대체
        console.error("employees_list 로딩 실패 (더미 데이터 사용):", err);
        const filteredData = dummy_employees.filter(emp =>
            (emp.emp_name.toLowerCase().includes(search_keyword.toLowerCase()) ||
                emp.dept_name.toLowerCase().includes(search_keyword.toLowerCase()) ||
                emp.position.toLowerCase().includes(search_keyword.toLowerCase()))
        );

        $.each(filteredData, function (i, row) {
            tbody += `<tr>
                        <td>${row.emp_id || ''}</td>
                        <td>${row.emp_name || ''}</td>
                        <td>${row.dept_name || ''}</td>
                        <td>${row.position || ''}</td>
                        <td>${row.hire_date || ''}</td>
                        <td>${row.status || ''}</td>
                        <td>${row.phone_number || ''}</td>
                      </tr>`;
        });
        tbody += `</tbody></table>`;
        return table + tbody;
    }
}


// ================================================================
// 2. 근태 현황 (Attendance)
// ================================================================

// 근태 현황 검색 폼
export function attendance_search_form() {
    const today = new Date().toISOString().substring(0, 7); // YYYY-MM
    return `<form data-file="hr" data-fn="attendance_list">
                <label>월 선택:
                    <input type="month" name="search_month" value="${today}" />
                </label>
                <label>검색:
                    <input type="text" name="search_keyword" placeholder="이름/상태 검색" />
                </label>
                <button type="submit" data-action="search" class="search_btn">검색</button>
            </form>`;
}

// 근태 현황 목록 전체 조회 (현재 월 데이터)
export async function attendance_listAll() {
    const today = new Date().toISOString().substring(0, 7);
    const formData = { search_month: today };
    return await attendance_fetch_data(formData);
}

// 근태 현황 조건 검색
export async function attendance_list(formData) {
    return await attendance_fetch_data(formData);
}

// 근태 데이터 AJAX 호출 및 HTML 생성 공통 함수
async function attendance_fetch_data(formData) {
    let table = `<table>
                    <thead>
                        <tr>
                            <th>날짜</th>
                            <th>사번</th>
                            <th>이름</th>
                            <th>출근 시간</th>
                            <th>퇴근 시간</th>
                            <th>근무 상태</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/attendance`, // HRController 호출
            method: 'GET',
            dataType: 'json',
            data: {
                month_start_date: formData.search_month ? formData.search_month + '-01' : null,
                search_keyword: formData.search_keyword
            }
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                const check_in_time = row.check_in ? row.check_in.substring(0, 5) : '-';
                const check_out_time = row.check_out ? row.check_out.substring(0, 5) : '-';

                let status_class = '';
                if (row.status === '지각' || row.status === '결근') {
                    status_class = 'status-alert';
                }

                tbody += `<tr>
                            <td>${row.att_date || '-'}</td>
                            <td>${row.emp_id || '-'}</td>
                            <td>${row.emp_name || '-'}</td>
                            <td>${check_in_time}</td>
                            <td>${check_out_time}</td>
                            <td class="${status_class}"><strong>${row.status || '-'}</strong></td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="6" style="text-align:center;">데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        console.error("attendance_list 로딩 실패:", err);
        // 오류 발생 시 오류 메시지 반환
        return table + `<tbody><tr><td colspan="6" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}


// ================================================================
// 3. 급여 대장 (Salary)
// ================================================================

// 급여 대장 검색 폼
export function salary_search_form() {
    const currentYear = new Date().getFullYear();
    return `<form data-file="hr" data-fn="salary_list">
                <label>년도 선택:
                    <input type="number" name="search_year" value="${currentYear}" placeholder="년도 입력 (예: 2025)" />
                </label>
                <button type="submit" data-action="search" class="search_btn">검색</button>
            </form>`;
}

// 급여 대장 목록 전체 조회 (더미 메시지)
export function salary_listAll() {
    // 실제 API 호출을 위한 뼈대
    // return salary_fetch_data({});
    return Promise.resolve("<h3>급여 대장 목록</h3><p>API 및 상세 구현 필요 (현재는 더미 메시지)</p>");
}
// 급여 대장 조건 검색 (더미 메시지)
export function salary_list(formData) {
    // 실제 API 호출을 위한 뼈대
    // return salary_fetch_data(formData);
    const year = formData.search_year || 'N/A';
    return Promise.resolve(`<h3>급여 대장 검색 결과 (년도: ${year})</h3><p>API 및 상세 구현 필요 (현재는 더미 메시지)</p>`);
}