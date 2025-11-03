
const API_BASE_URL = '/api/hr'; // HRController와 일치

// ----------------------------------------------------------------
// 1. 인사 관리 (Employees)
// ----------------------------------------------------------------


export async function employees_listAll() {
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
    let tbody;

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/employees`, // HRController 호출
            method: 'GET',
            dataType: 'json'
        });

        tbody = `<tbody>`;
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
        console.error("employees_listAll 로딩 실패:", err);
        return table + `<tbody><tr><td colspan="7" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}

/**
 * 직원 목록 조건 검색 (검색 버튼 클릭 시)
 * - 템플릿의 sale_list와 '유사하게' *async 키워드 없이* 작성
 * - [주의] 이 함수는 main.html의 await와 호환되지 않아 오류를 발생시킵니다.
 */

export function employees_list(formData) {
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

    $.ajax({
        url: `${API_BASE_URL}/employees`, // HRController 호출
        method: 'GET',
        dataType: 'json',
        data: {
            sort: sort,
            search: search_keyword
        }
    }).then(function (data) {
        table += `<tbody>`; // AJAX가 성공하면 나중에 이 부분이 실행됨
        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                table += `<tr>
                            <td>${row.emp_id || ''}</td>
                            <td>${row.emp_name || ''}</td>
                            <td>${row.dept_name || ''}</td>
                            <td>${row.position || ''}</td>
                            <td>${row.hire_date || ''}</td>
                            <td>${row.status || ''}</td>
                            <td>${row.phone_number || ''}</td>
                          </tr>`;
            });
        }
    });

    // 템플릿과 '유사하게' .then()이 실행되기 *전*에 <tbody>가 비어있는 테이블을 반환
    table += `</tbody></table>`;
    return table;
}



export function attendance_listAll() { }
export function attendance_list(formData) { }



export function salary_listAll() { }
export function salary_list(formData) { }



/**
검색 정렬, 등등 구현해야함
 */
