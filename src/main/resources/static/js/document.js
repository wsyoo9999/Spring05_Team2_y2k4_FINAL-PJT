export async function document_listAll(){


    let table = `<table>
                            <thead>
                            <tr>
                            <th>기안자</th>
                            <th>결재자</th>
                            <th>제목</th>
                            <th>작성일</th>
                            <th>결재일</th>
                            <th>결재 상태</th>
                            <th>카테고리</th>
                            <th>세부 카테고리</th>
                            <th>분류</th>
                            </tr>
                            </thead>
                        `;
    let tbody;

    const data = await $.ajax({
        url: '/api/doc/list',
        method: 'GET',
        dataType: 'json',

    });

    tbody= `<tbody>`;

    $.each(data, function (i, row){
        const { text: statusText, color: statusColor } = getStatusInfo(row.status);
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

        tbody += `<tr>
            <td data-value="${row.req_id}"
                style="cursor:pointer"
                onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                onmouseout="this.style.color=''; this.style.fontWeight='';"
                data-action="detail" data-file="document" data-fn="viewEmployee">
                ${row.req_name ?? ''}
            </td>
            <td data-value="${row.appr_id}"
                style="cursor:pointer"
                onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                onmouseout="this.style.color=''; this.style.fontWeight='';"
                data-action="detail" data-file="document" data-fn="viewEmployee">
                ${row.appr_name ?? ''}
            </td>
            <td data-value="${row.doc_id}"
                style="cursor:pointer"
                onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                onmouseout="this.style.color=''; this.style.fontWeight='';"
                data-action="detail" data-file="document" data-fn="viewDocument">
                ${row.title ?? ''} <!-- 여기 원래 doc_title 이었음, title 로 수정 -->
            </td>
            <td>
                ${row.req_date ?? ''}
            </td>
            <td>
                ${row.appr_date ?? ''}
            </td>
            <td style="color:${statusColor}; font-weight:600;">
                ${statusText}
            </td>
            <td>
                ${catLabel}
            </td>
            <td>
                ${tbLabel}
            </td>
            <td>
                ${row.cd_id}
            </td>
        </tr>`;
    });
    tbody += `</tbody></table>`;

    return table + tbody;
}

export async function  viewEmployee(id) {
    const base = './../popup/hr/viewEmployee.html';
    const url  = `${base}?emp_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'view_emp', features);
    if (child) child.focus();
}

export async function  viewDocument(id) {
    const base = './../popup/doc/viewDocument.html';
    const url  = `${base}?doc_id=${encodeURIComponent(id)}`;
    const features = 'width=760,height=850,resizable=no,scrollbars=yes';
    const child = window.open(url, 'view_doc', features);
    if (child) child.focus();
}

// 카테고리 / 세부 카테고리 라벨 매핑
const DOC_CAT_LABELS = {
    0: '재무',
    1: '판매/구매',
    2: '생산/제조',
    3: '재고',
    4: '인사'
};

const DOC_TB_LABELS = {
    0: { // 재무
        0: '회사 수익 관리',
        1: '회사 지출 관리'
    },
    1: { // 판매/구매
        0: '판매',
        1: '구매'
    },
    2: { // 생산/제조
        0: '작업 지시서'
    },
    3: { // 재고
        0: '재고 관련'
    },
    4: { // 인사
        0: '휴가 요청',  //
        1: '퇴사 요청'   //
    }
};

// 상태 표시용 헬퍼
function getStatusInfo(statusRaw) {
    const s = Number(statusRaw);
    switch (s) {
        case 0:
            return { text: '처리중', color: '#f39c12' }; // 주황
        case 1:
            return { text: '승인', color: '#27ae60' };   // 초록
        case 2:
            return { text: '반려', color: '#e74c3c' };   // 빨강
        default:
            return { text: String(statusRaw ?? ''), color: '#333333' };
    }
}

// 카테고리/세부 카테고리 표시용 헬퍼
function getCatLabel(catRaw) {
    const c = Number(catRaw);
    return DOC_CAT_LABELS[c] ?? String(catRaw ?? '');
}

function getTbLabel(catRaw, tbRaw) {
    const c = Number(catRaw);
    const t = Number(tbRaw);
    if (DOC_TB_LABELS[c] && DOC_TB_LABELS[c][t]) {
        return DOC_TB_LABELS[c][t];
    }
    return String(tbRaw ?? '');
}

//=======================================================================================//

// 검색 폼 HTML 생성
export function document_search_form() {
    const form = `
        <form data-file="document" data-fn="document_list">
            <div class="form-group">
                <label> 작성자 :
                    <!-- 작성자: emp_id 대신 문서의 기안자(req_id)라고 가정 -->
                    <select name="req_id" id="doc_req_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 결재자 :
                    <select name="appr_id" id="doc_appr_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 작성일:
                    <input type="date" name="req_date" />
                </label>
            </div>
            <div class="form-group">
                <label> 결재일:
                    <input type="date" name="appr_date" />
                </label>
            </div>
            <div class="form-group">
                <label> 상태:
                    <select name="status">
                        <option value="">전체</option>
                        <option value="0">대기</option>
                        <option value="1">승인</option>
                        <option value="2">반려</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 카테고리:
                    <select name="cat_id" id="doc_cat_id">
                        <option value="">전체</option>
                        <option value="0">재무</option>
                        <option value="1">판매/구매</option>
                        <option value="2">생산/제조</option>
                        <option value="3">재고</option>
                        <option value="4">인사</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 세부 카테고리:
                    <select name="tb_id" id="doc_tb_id">
                        <option value="">전체</option>
                        <!-- cat_id 선택 시 JS에서 자동으로 채워짐 -->
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 분류:
                    <select name="cd_id">
                        <option value="">전체</option>
                        <option value="0">추가</option>
                        <option value="1">수정</option>
                        <option value="2">삭제</option>
                    </select>
                </label>
            </div>
            <button type="button" data-action="search" class="search_btn" data-file="document" data-fn="document_list">
                <i class="fas fa-search"></i> 검색
            </button>
        </form>
    `;

    // 폼이 DOM에 붙은 뒤에 이벤트/옵션을 셋업하기 위해 살짝 뒤에 실행
    setTimeout(() => bind_document_search_form_by_fn('document_list'), 0);

    return form;
}

const doc_tb_map = {
    0: [ // 재무
        { value: '0', label: '회사 수익 관리' },
        { value: '1', label: '회사 지출 관리' }
    ],
    1: [ // 판매/구매
        { value: '0', label: '판매' },
        { value: '1', label: '구매' }
    ],
    2: [ // 생산/제조
        { value: '0', label: '작업 지시서' }
    ],
    3: [ // 재고
        { value: '0', label: '재고 관련' }
    ],
    4: [ // 인사
        { value: '0', label: '휴가 요청' },
        { value: '1', label: '퇴사 요청' }
    ]
};

function bind_document_search_form_by_fn(fnName) {
    const container = document.querySelector('.search-form');
    if (!container) return;

    const form = container.querySelector(`form[data-file="document"][data-fn="${fnName}"]`);
    if (!form) return;

    const cat_select = form.querySelector('select[name="cat_id"]');
    const tb_select  = form.querySelector('select[name="tb_id"]');
    const req_select = form.querySelector('select[name="req_id"]');
    const appr_select = form.querySelector('select[name="appr_id"]');

    if (cat_select && tb_select) {
        cat_select.addEventListener('change', () => {
            update_tb_options(cat_select.value, tb_select);
        });
        update_tb_options(cat_select.value, tb_select);
    }

    if (req_select) {
        load_req_options(req_select, appr_select);
    }
}

function update_tb_options(cat_id_value, tb_select) {
    tb_select.innerHTML = '<option value="">전체</option>';

    if (cat_id_value === '' || cat_id_value === null || cat_id_value === undefined) {
        return; // 전체 선택일 때는 기본 "전체"만
    }

    const items = doc_tb_map[cat_id_value];
    if (!items) return;

    items.forEach(item => {
        const opt = document.createElement('option');
        opt.value = item.value;
        opt.textContent = item.label;
        tb_select.appendChild(opt);
    });
}
async function load_req_options(req_select, appr_select) {
    try {
        const res = await fetch('/api/hr/employees');
        if (!res.ok) {
            console.error('직원 목록 조회 실패:', res.status);
            return;
        }
        const list = await res.json();

        // 기본 옵션 초기화
        req_select.innerHTML = '<option value="">전체</option>';
        if (appr_select) {
            appr_select.innerHTML = '<option value="">전체</option>';
        }

        list.forEach(emp => {
            const opt1 = document.createElement('option');
            opt1.value = emp.emp_id;
            opt1.textContent = `${emp.emp_name} (${emp.emp_id})`;
            req_select.appendChild(opt1);

            if (appr_select) {
                const opt2 = document.createElement('option');
                opt2.value = emp.emp_id;
                opt2.textContent = `${emp.emp_name} (${emp.emp_id})`;
                appr_select.appendChild(opt2);
            }
        });
    } catch (err) {
        console.error('작성자/결재자 옵션 로딩 중 오류', err);
    }
}

// 자신의 결재 목록 조회 함수
export async function document_listMy(formData) {
    let table = `<table>
        <thead>
        <tr>
        <th>기안자</th>
        <th>결재자</th>
        <th>제목</th>
        <th>작성일</th>
        <th>결재일</th>
        <th>결재 상태</th>
        <th>카테고리</th>
        <th>세부 카테고리</th>
        <th>분류</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    try {
        const data = await $.ajax({
            url: '/api/doc/mylist',
            method: 'GET',
            dataType: 'json',
            data: formData // [추가] 검색 조건 전달
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row){
                const { text: statusText, color: statusColor } = getStatusInfo(row.status);
                const catLabel = getCatLabel(row.cat_id);
                const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

                tbody += `<tr>
                    <td data-value="${row.req_id}" style="cursor:pointer"
                        onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                        onmouseout="this.style.color=''; this.style.fontWeight='';"
                        data-action="detail" data-file="document" data-fn="viewEmployee">
                        ${row.req_name ?? ''}
                    </td>
                    <td data-value="${row.appr_id}" style="cursor:pointer"
                        onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                        onmouseout="this.style.color=''; this.style.fontWeight='';"
                        data-action="detail" data-file="document" data-fn="viewEmployee">
                        ${row.appr_name ?? ''}
                    </td>
                    <td data-value="${row.doc_id}" style="cursor:pointer"
                        onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                        onmouseout="this.style.color=''; this.style.fontWeight='';"
                        data-action="detail" data-file="document" data-fn="viewDocument">
                        ${row.title ?? ''}
                    </td>
                    <td>${row.req_date ?? ''}</td>
                    <td>${row.appr_date ?? ''}</td>
                    <td style="color:${statusColor}; font-weight:600;">${statusText}</td>
                    <td>${catLabel}</td>
                    <td>${tbLabel}</td>
                    <td>${row.cd_id}</td>
                </tr>`;
            });
        } else {
            // [추가] 결과가 없을 때 메시지 표시
            tbody += `<tr><td colspan="9" style="text-align:center;">검색 결과가 없습니다.</td></tr>`;
        }
    } catch(err) {
        console.error(err);
    }
    tbody += `</tbody></table>`;

    return table + tbody;
}

// 문서 목록 조건 검색 함수
export async function document_list(formData) {
    let table = `<table>
        <thead>
        <tr>
        <th>기안자</th>
        <th>결재자</th>
        <th>제목</th>
        <th>작성일</th>
        <th>결재일</th>
        <th>결재 상태</th>
        <th>카테고리</th>
        <th>세부 카테고리</th>
        <th>분류</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    try {
        const data = await $.ajax({
            url: '/api/doc/list', // 검색도 같은 list API 사용
            method: 'GET',
            dataType: 'json',
            data: formData // 폼에서 입력받은 검색 조건 전달
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                const { text: statusText, color: statusColor } = getStatusInfo(row.status);
                const catLabel = getCatLabel(row.cat_id);
                const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

                tbody += `<tr>
                    <td data-value="${row.req_id}"
                        style="cursor:pointer"
                        onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                        onmouseout="this.style.color=''; this.style.fontWeight='';"
                        data-action="detail" data-file="document" data-fn="viewEmployee">
                        ${row.req_name ?? ''}
                    </td>
                    <td data-value="${row.appr_id}"
                        style="cursor:pointer"
                        onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                        onmouseout="this.style.color=''; this.style.fontWeight='';"
                        data-action="detail" data-file="document" data-fn="viewEmployee">
                        ${row.appr_name ?? ''}
                    </td>
                    <td data-value="${row.doc_id}"
                        style="cursor:pointer"
                        onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                        onmouseout="this.style.color=''; this.style.fontWeight='';"
                        data-action="detail" data-file="document" data-fn="viewDocument">
                        ${row.title ?? ''}
                    </td>
                    <td>${row.req_date ?? ''}</td>
                    <td>${row.appr_date ?? ''}</td>
                    <td style="color:${statusColor}; font-weight:600;">${statusText}</td>
                    <td>${catLabel}</td>
                    <td>${tbLabel}</td>
                    <td>${row.cd_id}</td>
                </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="9" style="text-align:center;">검색 결과가 없습니다.</td></tr>`;
        }
    } catch (err) {
        console.error("검색 실패:", err);
        tbody += `<tr><td colspan="9" style="text-align:center; color:red;">데이터를 불러오는데 실패했습니다.</td></tr>`;
    }

    tbody += `</tbody></table>`;
    return table + tbody;
}

export function document_my_search_form() {
    // data-fn="document_listMy" 로 설정하여 검색 버튼 클릭 시 해당 함수가 실행되도록 함
    const form = `
        <form data-file="document" data-fn="document_listMy">
            <div class="form-group">
                <label> 작성자 :
                    <select name="req_id" class="doc_req_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 결재자 :
                    <select name="appr_id" class="doc_appr_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 작성일:
                    <input type="date" name="req_date" />
                </label>
            </div>
            <div class="form-group">
                <label> 결재일:
                    <input type="date" name="appr_date" />
                </label>
            </div>
            <div class="form-group">
                <label> 상태:
                    <select name="status">
                        <option value="">전체</option>
                        <option value="0">대기</option>
                        <option value="1">승인</option>
                        <option value="2">반려</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 카테고리:
                    <select name="cat_id" class="doc_cat_id">
                        <option value="">전체</option>
                        <option value="0">재무</option>
                        <option value="1">판매/구매</option>
                        <option value="2">생산/제조</option>
                        <option value="3">재고</option>
                        <option value="4">인사</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 세부 카테고리:
                    <select name="tb_id" class="doc_tb_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 분류:
                    <select name="cd_id">
                        <option value="">전체</option>
                        <option value="0">추가</option>
                        <option value="1">수정</option>
                        <option value="2">삭제</option>
                    </select>
                </label>
            </div>
            <button type="button" data-action="search" class="search_btn" data-file="document" data-fn="document_listMy">
                <i class="fas fa-search"></i> 검색
            </button>
        </form>
    `;

    setTimeout(() => bind_document_search_form_by_fn('document_listMy'), 0);

    return form;
}