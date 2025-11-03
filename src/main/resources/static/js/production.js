export function search_form() {
    // 기본적으로 작업 지시서 검색 폼 반환
    return work_order_search_form();
}

// 1. 작업 지시서 전체 목록 조회
export async function work_order_listAll() {
    let table = `<table class="work-order-table">
                    <thead>
                        <tr>
                            <th>작업지시번호</th>
                            <th>물품번호</th>
                            <th>시작일</th>
                            <th>완료일</th>
                            <th>목표수량</th>
                            <th>양품수량</th>
                            <th>불량수량</th>
                            <th>상태</th>
                            <th>관리</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: '/api/production/work_order',
            method: 'GET',
            dataType: 'json'
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                const statusClass = getStatusClass(row.order_status);
                const progress = row.target_quantity > 0
                    ? Math.round((row.good_quantity / row.target_quantity) * 100)
                    : 0;

                tbody += `<tr data-order-id="${row.order_id}">
                            <td><strong>${row.order_id}</strong></td>
                            <td>${row.item_id}</td>
                            <td>${formatDate(row.start_date)}</td>
                            <td>${formatDate(row.due_date)}</td>
                            <td>${numberFormat(row.target_quantity)}</td>
                            <td class="good-qty">${numberFormat(row.good_quantity)}</td>
                            <td class="defect-qty">${numberFormat(row.defect_quantity)}</td>
                            <td><span class="status-badge ${statusClass}">${row.order_status}</span></td>
                            <td>
                                <button class="btn-detail" data-order-id="${row.order_id}">상세</button>
                                <button class="btn-result" data-order-id="${row.order_id}">실적등록</button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="9" class="no-data">조회된 작업 지시서가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('작업 지시서 목록 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="9" class="error">데이터를 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 2. 작업 지시서 검색 폼
export function work_order_search_form() {
    const search_bar = `<form data-file="production" data-fn="work_order_list">
                            <label>상태:
                                <select name="order_status">
                                    <option value="">전체</option>
                                    <option value="대기">대기</option>
                                    <option value="진행중">진행중</option>
                                    <option value="완료">완료</option>
                                </select>
                            </label>
                            <label>물품번호:
                                <input type="text" name="item_id" placeholder="물품번호 입력" />
                            </label>
                            <label>시작일:
                                <input type="date" name="start_date" />
                            </label>
                            <label>완료일:
                                <input type="date" name="due_date" />
                            </label>
                            <button type="submit" class="search_btn">검색</button>
                        </form>`;

    return search_bar;
}



// 3. 작업 지시서 조건 검색
export async function work_order_list(formData) {
    const order_status = formData.order_status || '';
    const item_id = formData.item_id || '';
    const start_date = formData.start_date || '';
    const due_date = formData.due_date || '';

    let table = `<table class="work-order-table">
                    <thead>
                        <tr>
                            <th>작업지시번호</th>
                            <th>물품번호</th>
                            <th>시작일</th>
                            <th>완료일</th>
                            <th>목표수량</th>
                            <th>양품수량</th>
                            <th>불량수량</th>
                            <th>상태</th>
                            <th>관리</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: '/api/production/work_order',
            method: 'GET',
            dataType: 'json',
            data: {
                order_status: order_status,
                item_id: item_id,
                start_date: start_date,
                due_date: due_date
            }
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                const statusClass = getStatusClass(row.order_status);

                tbody += `<tr data-order-id="${row.order_id}">
                            <td><strong>${row.order_id}</strong></td>
                            <td>${row.item_id}</td>
                            <td>${formatDate(row.start_date)}</td>
                            <td>${formatDate(row.due_date)}</td>
                            <td>${numberFormat(row.target_quantity)}</td>
                            <td class="good-qty">${numberFormat(row.good_quantity)}</td>
                            <td class="defect-qty">${numberFormat(row.defect_quantity)}</td>
                            <td><span class="status-badge ${statusClass}">${row.order_status}</span></td>
                            <td>
                                <button class="btn-detail" data-order-id="${row.order_id}">상세</button>
                                <button class="btn-result" data-order-id="${row.order_id}">실적등록</button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="9" class="no-data">검색 결과가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('작업 지시서 검색 실패:', error);
        tbody = `<tbody><tr><td colspan="9" class="error">검색에 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}



// 4. 작업 지시서 상세 조회
export async function work_order_detail(order_id) {
    let detailHtml = '';

    try {
        const data = await $.ajax({
            url: `/api/production/work_order/${order_id}`,
            method: 'GET',
            dataType: 'json'
        });

        const progress = data.target_quantity > 0
            ? Math.round((data.good_quantity / data.target_quantity) * 100)
            : 0;

        detailHtml = `
            <div class="work-order-detail-card">
                <div class="detail-header">
                    <h3>📋 작업 지시서 상세 정보</h3>
                    <span class="status-badge ${getStatusClass(data.order_status)}">${data.order_status}</span>
                </div>
                
                <div class="detail-body">
                    <div class="info-section">
                        <h4>기본 정보</h4>
                        <div class="info-grid">
                            <div class="info-item">
                                <span class="label">작업지시번호</span>
                                <span class="value">${data.order_id}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">물품번호</span>
                                <span class="value">${data.item_id}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">시작일</span>
                                <span class="value">${formatDate(data.start_date)}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">완료일</span>
                                <span class="value">${formatDate(data.due_date)}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="quantity-section">
                        <h4>생산 수량</h4>
                        <div class="quantity-grid">
                            <div class="quantity-card target">
                                <span class="quantity-label">목표수량</span>
                                <span class="quantity-value">${numberFormat(data.target_quantity)}</span>
                            </div>
                            <div class="quantity-card good">
                                <span class="quantity-label">양품수량</span>
                                <span class="quantity-value">${numberFormat(data.good_quantity)}</span>
                            </div>
                            <div class="quantity-card defect">
                                <span class="quantity-label">불량수량</span>
                                <span class="quantity-value">${numberFormat(data.defect_quantity)}</span>
                            </div>
                        </div>
                        
                        <div class="progress-container">
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: ${progress}%"></div>
                            </div>
                            <span class="progress-text">${progress}% 완료</span>
                        </div>
                    </div>
                    
                    <div class="action-buttons">
                        <button class="btn-primary btn-register-result" data-order-id="${data.order_id}">생산 실적 등록</button>
                        <button class="btn-warning btn-register-defect" data-order-id="${data.order_id}">불량 등록</button>
                        <button class="btn-info btn-view-lots" data-order-id="${data.order_id}">Lot 조회</button>
                        <button class="btn-secondary btn-view-bom" data-item-id="${data.item_id}">BOM 조회</button>
                    </div>
                </div>
            </div>
        `;

    } catch (error) {
        console.error('작업 지시서 상세 조회 실패:', error);
        detailHtml = `<div class="error-message">상세 정보를 불러오는데 실패했습니다.</div>`;
    }

    return detailHtml;
}



// 5. BOM(자재 명세서) 전체 조회
export async function bom_listAll() {
    let table = `<table class="bom-table">
                    <thead>
                        <tr>
                            <th>BOM ID</th>
                            <th>원자재 코드</th>
                            <th>물품번호</th>
                            <th>소요량</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: '/api/production/bom',
            method: 'GET',
            dataType: 'json'
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                tbody += `<tr>
                            <td>${row.bom_id}</td>
                            <td>${row.raw_materials_code}</td>
                            <td>${row.item_id}</td>
                            <td><strong>${numberFormat(row.required_quantity)}</strong></td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="4" class="no-data">BOM 정보가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('BOM 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="4" class="error">BOM 정보를 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}



// 6. BOM 검색 폼
export function bom_search_form() {
    const search_bar = `<form data-file="production" data-fn="bom_list">
                            <label>물품번호:
                                <input type="text" name="item_id" placeholder="물품번호 입력" />
                            </label>
                            <button type="submit" class="search_btn">검색</button>
                        </form>`;

    return search_bar;
}



// 7. BOM 조건 검색 (물품별)
export async function bom_list(formData) {
    const item_id = formData.item_id || '';

    let table = `<table class="bom-table">
                    <thead>
                        <tr>
                            <th>BOM ID</th>
                            <th>원자재 코드</th>
                            <th>물품번호</th>
                            <th>소요량</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: '/api/production/bom',
            method: 'GET',
            dataType: 'json',
            data: { item_id: item_id }
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                tbody += `<tr>
                            <td>${row.bom_id}</td>
                            <td>${row.raw_materials_code}</td>
                            <td>${row.item_id}</td>
                            <td><strong>${numberFormat(row.required_quantity)}</strong></td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="4" class="no-data">검색 결과가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('BOM 검색 실패:', error);
        tbody = `<tbody><tr><td colspan="4" class="error">검색에 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}

// 유틸리티 함수들

// 날짜 포맷 (YYYY-MM-DD)
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 숫자 천단위 콤마
function numberFormat(num) {
    if (num === null || num === undefined) return '0';
    return parseInt(num).toLocaleString('ko-KR');
}

// 작업 상태에 따른 CSS 클래스 반환
function getStatusClass(status) {
    switch(status) {
        case '대기': return 'status-waiting';
        case '진행중': return 'status-progress';
        case '완료': return 'status-complete';
        default: return 'status-default';
    }
}

// 설비 상태에 따른 CSS 클래스 반환
function getEquipmentStatusClass(status) {
    switch(status) {
        case '정상': return 'status-normal';
        case '점검중': return 'status-checking';
        case '고장': return 'status-broken';
        default: return 'status-default';
    }
}