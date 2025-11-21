// js/finance.js

// ================== 공통 라벨/맵 ================== //

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
        0: '휴가/퇴직 처리'
    }
};

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
        { value: '0', label: '휴가/퇴직 처리' }
    ]
};

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

// Unpaid 종류/상태 라벨
function getTypeInfo(type) {
    const c = Number(type);
    switch (c) {
        case 1:
            return { text: '수익', color: '#27ae60' };   // 초록
        case 2:
            return { text: '지출', color: '#e74c3c' };   // 빨강
        default:
            return { text: String(type ?? ''), color: '#333333' };
    }
}

function getStatusInfo(statusRaw) {
    const s = Number(statusRaw);
    switch (s) {
        case 0:
            return { text: '미정산', color: '#f39c12' }; // 주황
        case 1:
            return { text: '정산 완료', color: '#27ae60' };   // 초록
        case 2:
            return { text: '취소(거래 취소)', color: '#e74c3c' };   // 빨강
        default:
            return { text: String(statusRaw ?? ''), color: '#333333' };
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

// ================== PROFIT (수익) ================== //

export async function profit_listAll() {
    let table = `<table>
        <thead>
        <tr>
            <th>결재일</th>
            <th>카테고리</th>
            <th>세부 카테고리</th>
            <th>수익액</th>
            <th>비고</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    const data = await $.ajax({
        url: '/api/finance/profit/list',
        method: 'GET',
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

        tbody += `<tr>
            <td>${row.profit_date ?? ''}</td>
            <td>${catLabel}</td>
            <td>${tbLabel}</td>
            <td>${row.profit}</td>
            <td>${row.profit_comment ?? ''}</td>
        </tr>`;
    });

    tbody += `</tbody></table>`;
    return table + tbody;
}

export async function profit_list(formData) {
    const cat_id = formData.cat_id;
    const tb_id = formData.tb_id;
    const from_date = formData.from_date;
    const to_date = formData.to_date;

    let table = `<table>
        <thead>
        <tr>
            <th>결재일</th>
            <th>카테고리</th>
            <th>세부 카테고리</th>
            <th>수익액</th>
            <th>비고</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    const data = await $.ajax({
        url: '/api/finance/profit/list',
        method: 'GET',
        data: {
            cat_id: cat_id,
            tb_id: tb_id,
            from_date: from_date,
            to_date: to_date
        },
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

        tbody += `<tr>
            <td>${row.profit_date ?? ''}</td>
            <td>${catLabel}</td>
            <td>${tbLabel}</td>
            <td>${row.profit}</td>
            <td>${row.profit_comment ?? ''}</td>
        </tr>`;
    });

    tbody += `</tbody></table>`;
    return table + tbody;
}

// ================== SPEND (지출) ================== //

export async function spend_listAll() {
    let table = `<table>
        <thead>
        <tr>
            <th>결재일</th>
            <th>카테고리</th>
            <th>세부 카테고리</th>
            <th>지출액</th>
            <th>비고</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    const data = await $.ajax({
        url: '/api/finance/spend/list',
        method: 'GET',
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

        tbody += `<tr>
            <td>${row.spend_date ?? ''}</td>
            <td>${catLabel}</td>
            <td>${tbLabel}</td>
            <td>${row.spend}</td>
            <td>${row.spend_comment ?? ''}</td>
        </tr>`;
    });

    tbody += `</tbody></table>`;
    return table + tbody;
}

export async function spend_list(formData) {
    const cat_id = formData.cat_id;
    const tb_id = formData.tb_id;
    const from_date = formData.from_date;
    const to_date = formData.to_date;

    let table = `<table>
        <thead>
        <tr>
            <th>결재일</th>
            <th>카테고리</th>
            <th>세부 카테고리</th>
            <th>지출액</th>
            <th>비고</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    const data = await $.ajax({
        url: '/api/finance/spend/list',
        method: 'GET',
        data: {
            cat_id: cat_id,
            tb_id: tb_id,
            from_date: from_date,
            to_date: to_date
        },
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);

        tbody += `<tr>
            <td>${row.spend_date ?? ''}</td>
            <td>${catLabel}</td>
            <td>${tbLabel}</td>
            <td>${row.spend}</td>
            <td>${row.spend_comment ?? ''}</td>
        </tr>`;
    });

    tbody += `</tbody></table>`;
    return table + tbody;
}

// ================== UNPAID (미정산) ================== //

export async function unpaid_listAll() {
    let table = `<table>
        <thead>
        <tr>
            <th>등록일</th>
            <th>정산일</th>
            <th>카테고리</th>
            <th>세부 카테고리</th>
            <th>미정산액</th>
            <th>종류</th>
            <th>현 상태</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    const data = await $.ajax({
        url: '/api/finance/unpaid/list',
        method: 'GET',
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);
        const { text: typeText, color: typeColor } = getTypeInfo(row.type);
        const { text: statusText, color: statusColor } = getStatusInfo(row.status);

        tbody += `<tr>
            <td>${row.unpaid_date ?? ''}</td>
            <td>${row.paid_date ?? ''}</td>
            <td>${catLabel}</td>
            <td>${tbLabel}</td>
            <td style="color:${typeColor}; font-weight:600;">
                ${row.cost}
            </td>
            <td>${typeText}</td>
            <td style="color:${statusColor}; font-weight:600;">
                ${statusText}
            </td>
        </tr>`;
    });

    tbody += `</tbody></table>`;
    return table + tbody;
}

export async function unpaid_list(formData) {
    const cat_id = formData.cat_id;
    const tb_id = formData.tb_id;
    const type = formData.type;
    const status = formData.status;

    let table = `<table>
        <thead>
        <tr>
            <th>등록일</th>
            <th>정산일</th>
            <th>카테고리</th>
            <th>세부 카테고리</th>
            <th>미정산액</th>
            <th>종류</th>
            <th>현 상태</th>
        </tr>
        </thead>
    `;
    let tbody = `<tbody>`;

    const data = await $.ajax({
        url: '/api/finance/unpaid/list',
        method: 'GET',
        data: {
            cat_id: cat_id,
            tb_id: tb_id,
            type: type,
            status: status
        },
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        const catLabel = getCatLabel(row.cat_id);
        const tbLabel  = getTbLabel(row.cat_id, row.tb_id);
        const { text: typeText, color: typeColor } = getTypeInfo(row.type);
        const { text: statusText, color: statusColor } = getStatusInfo(row.status);

        tbody += `<tr>
            <td>${row.unpaid_date ?? ''}</td>
            <td>${row.paid_date ?? ''}</td>
            <td>${catLabel}</td>
            <td>${tbLabel}</td>
            <td style="color:${typeColor}; font-weight:600;">
                ${row.cost}
            </td>
            <td>${typeText}</td>
            <td style="color:${statusColor}; font-weight:600;">
                ${statusText}
            </td>
        </tr>`;
    });

    tbody += `</tbody></table>`;
    return table + tbody;
}

// ================== 검색 폼들 ================== //

export function profit_search_form() {
    const form = `
        <form data-file="profit" data-fn="profit_list">
            <div class="form-group">
                <label> 카테고리:
                    <select name="cat_id">
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
                    <select name="tb_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>   
            <div class="form-group">
                <label> 날짜 범위(시작):
                    <input type="date" name="from_date" />
                </label>
            </div>
            <div class="form-group">
                <label> 날짜 범위(끝):
                    <input type="date" name="to_date" />
                </label>
            </div>
            <button type="button" data-action="search"
                    class="search_btn"
                    data-file="finance" data-fn="profit_list">
                <i class="fas fa-search"></i> 검색
            </button>
        </form>
    `;

    setTimeout(bind_profit_search_form, 0);
    return form;
}

export function spend_search_form() {
    const form = `
        <form data-file="spend" data-fn="spend_list">
            <div class="form-group">
                <label> 카테고리:
                    <select name="cat_id">
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
                    <select name="tb_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>   
            <div class="form-group">
                <label> 날짜 범위(시작):
                    <input type="date" name="from_date" />
                </label>
            </div>
            <div class="form-group">
                <label> 날짜 범위(끝):
                    <input type="date" name="to_date" />
                </label>
            </div>
            <button type="button" data-action="search"
                    class="search_btn"
                    data-file="finance" data-fn="spend_list">
                <i class="fas fa-search"></i> 검색
            </button>
        </form>
    `;

    setTimeout(bind_spend_search_form, 0);
    return form;
}

export function unpaid_search_form() {
    const form = `
        <form data-file="unpaid" data-fn="unpaid_list">
            <div class="form-group">
                <label> 카테고리:
                    <select name="cat_id">
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
                    <select name="tb_id">
                        <option value="">전체</option>
                    </select>
                </label>
            </div>   
            <div class="form-group">
                <label> 종류:
                    <select name="type">
                        <option value="">전체</option>
                        <option value="1">수익</option>
                        <option value="2">지출</option>
                    </select>
                </label>
            </div>
            <div class="form-group">
                <label> 현상태:
                    <select name="status">
                        <option value="">전체</option>
                        <option value="0">미정산</option>
                        <option value="1">정산 완료</option>
                        <option value="2">취소(계약 취소)</option>
                    </select>
                </label>
            </div>
            <button type="button" data-action="search"
                    class="search_btn"
                    data-file="finance" data-fn="unpaid_list">
                <i class="fas fa-search"></i> 검색
            </button>
        </form>
    `;

    setTimeout(bind_unpaid_search_form, 0);
    return form;
}

// ================== 검색 폼 바인딩 ================== //

function bind_profit_search_form() {
    const container = document.querySelector('.search-form');
    if (!container) return;

    const form = container.querySelector('form[data-file="profit"][data-fn="profit_list"]');
    if (!form) return;

    const cat_select = form.querySelector('select[name="cat_id"]');
    const tb_select  = form.querySelector('select[name="tb_id"]');

    if (cat_select && tb_select) {
        cat_select.addEventListener('change', () => {
            const val = cat_select.value;
            update_tb_options(val, tb_select);
        });
        update_tb_options(cat_select.value, tb_select);
    }
}

function bind_spend_search_form() {
    const container = document.querySelector('.search-form');
    if (!container) return;

    const form = container.querySelector('form[data-file="spend"][data-fn="spend_list"]');
    if (!form) return;

    const cat_select = form.querySelector('select[name="cat_id"]');
    const tb_select  = form.querySelector('select[name="tb_id"]');

    if (cat_select && tb_select) {
        cat_select.addEventListener('change', () => {
            const val = cat_select.value;
            update_tb_options(val, tb_select);
        });
        update_tb_options(cat_select.value, tb_select);
    }
}

function bind_unpaid_search_form() {
    const container = document.querySelector('.search-form');
    if (!container) return;

    const form = container.querySelector('form[data-file="unpaid"][data-fn="unpaid_list"]');
    if (!form) return;

    const cat_select = form.querySelector('select[name="cat_id"]');
    const tb_select  = form.querySelector('select[name="tb_id"]');

    if (cat_select && tb_select) {
        cat_select.addEventListener('change', () => {
            const val = cat_select.value;
            update_tb_options(val, tb_select);
        });
        update_tb_options(cat_select.value, tb_select);
    }
}
