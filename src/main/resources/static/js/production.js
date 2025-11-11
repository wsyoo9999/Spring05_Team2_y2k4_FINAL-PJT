// 1. 작업 지시서 전체 목록 조회
export async function work_order_listAll() {
    let table = `<table>
                    <thead>
                        <tr>
                            <th>작업지시번호</th>
                            <th>제품코드</th>
                            <th>시작일</th>
                            <th>완료일</th>
                            <th>목표수량</th>
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
                // const statusClass = getStatusClass(row.order_status); // (추후 CSS 구현 시)

                tbody += `<tr data-work-order-id="${row.work_order_id}">
                            <td><strong>${row.work_order_id}</strong></td>
                            <td>${row.stock_id ?? '-'}</td>
                            <td>${formatDate(row.start_date)}</td>
                            <td>${formatDate(row.due_date)}</td>
                            <td>${numberFormat(row.target_qty)}</td>
                            <td class="actions">
                                <button class="btn-detail"
                                        data-action="detail"
                                        data-file="production"
                                        data-fn="work_order_detail_popup"
                                        data-work-order-id="${row.work_order_id}"
                                        title="상세 보기">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="6" style="text-align:center;">조회된 작업 지시서가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

        // 새 디자인의 '추가' 버튼을 테이블 하단에 추가
        // main.html에 정의된 .table-actions-footer와 .action-button 스타일을 사용
        const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="production" data-fn="addWorkOrder">
                    <i class="fas fa-plus-circle"></i> 작업 지시서 추가
                </button>
            </div>
        `;
        return actionRow + table + tbody;

    } catch (error) {
        console.error('작업 지시서 목록 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="6" style="text-align:center; color:red;">데이터를 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 2. 작업 지시서 검색 폼
export function work_order_search_form() {
    // main.html의 .search-form 디자인에 맞게 수정
    const search_bar = `
        <div class="form-group">
            <label for="order_status">상태</label>
            <select id="order_status" name="order_status">
                <option value="">전체</option>
                <option value="대기">대기</option>
                <option value="진행중">진행중</option>
                <option value="완료">완료</option>
            </select>
        </div>
        <div class="form-group">
            <label for="stock_id">제품코드</label>
            <input type="text" id="stock_id" name="stock_id" placeholder="제품코드 입력" />
        </div>
        <div class="form-group">
            <label for="start_date">시작일</label>
            <input type="date" id="start_date" name="start_date" />
        </div>
        <div class="form-group">
            <label for="due_date">완료일</label>
            <input type="date" id="due_date" name="due_date" />
        </div>
        <button type="submit" data-action="search" data-file="production" data-fn="work_order_list">
            <i class="fas fa-search"></i> 검색
        </button>
    `;
    return search_bar;
}


// 3. 작업 지시서 조건 검색
export async function work_order_list(formData) {
    const order_status = formData.order_status || '';
    const stock_id = formData.stock_id || '';
    const start_date = formData.start_date || '';
    const due_date = formData.due_date || '';

    let table = `<table>
                    <thead>
                        <tr>
                            <th>작업지시번호</th>
                            <th>제품코드</th>
                            <th>시작일</th>
                            <th>완료일</th>
                            <th>목표수량</th>
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
                stock_id: stock_id,
                start_date: start_date,
                due_date: due_date
            }
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                // const statusClass = getStatusClass(row.order_status);

                tbody += `<tr data-work-order-id="${row.work_order_id}">
                            <td><strong>${row.work_order_id}</strong></td>
                            <td>${row.stock_id ?? '-'}</td>
                            <td>${formatDate(row.start_date)}</td>
                            <td>${formatDate(row.due_date)}</td>
                            <td>${numberFormat(row.target_qty)}</td>
                            <td class="actions">
                                <button class="btn-detail"
                                        data-action="detail"
                                        data-file="production"
                                        data-fn="work_order_detail_popup"
                                        data-work-order-id="${row.work_order_id}"
                                        title="상세 보기">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="6" style="text-align:center;">검색 결과가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

        // listAll과 동일하게 '추가' 버튼 추가
        const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="production" data-fn="addWorkOrder">
                    <i class="fas fa-plus-circle"></i> 작업 지시서 추가
                </button>
            </div>
        `;
        return actionRow + table + tbody;

    } catch (error) {
        console.error('작업 지시서 검색 실패:', error);
        tbody = `<tbody><tr><td colspan="6" style="text-align:center; color:red;">검색에 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 4. 작업 지시서 상세 조회
export async function work_order_detail(work_order_id) {
    let detailHtml = '';

    try {
        const data = await $.ajax({
            url: `/api/production/work_order/${work_order_id}`,
            method: 'GET',
            dataType: 'json'
        });

        const progress = data.target_qty > 0
            ? Math.round((data.good_qty / data.target_qty) * 100)
            : 0;

        detailHtml = `
            <div class="work-order-detail-card">
                <div class="detail-header">
                    <h3>📋 작업 지시서 상세 정보</h3>
                    <span class="status-badge ${getStatusClass(data.order_status)}">${data.order_status ?? '-'}</span>
                </div>
                
                <div class="detail-body">
                    <div class="info-section">
                        <h4>기본 정보</h4>
                        <div class="info-grid">
                            <div class="info-item">
                                <span class="label">작업지시번호</span>
                                <span class="value">${data.work_order_id}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">품목번호</span>
                                <span class="value">${data.stock_id ?? '-'}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">담당자</span>
                                <span class="value">${data.emp_id ?? '-'}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">시작일</span>
                                <span class="value">${formatDate(data.start_date)}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">완료일</span>
                                <span class="value">${formatDate(data.due_date)}</span>
                            </div>
                            <div class="info-item">
                                <span class="label">요청일시</span>
                                <span class="value">${formatDateTime(data.request_date)}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="quantity-section">
                        <h4>생산 수량</h4>
                        <div class="quantity-grid">
                            <div class="quantity-card target">
                                <span class="quantity-label">목표수량</span>
                                <span class="quantity-value">${numberFormat(data.target_qty)}</span>
                            </div>
                            <div class="quantity-card good">
                                <span class="quantity-label">양품수량</span>
                                <span class="quantity-value">${numberFormat(data.good_qty)}</span>
                            </div>
                            <div class="quantity-card defect">
                                <span class="quantity-label">불량수량</span>
                                <span class="quantity-value">${numberFormat(data.defect_qty)}</span>
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
                        <button class="btn-primary btn-register-result" data-work-order-id="${data.work_order_id}">생산 실적 등록</button>
                        <button class="btn-warning btn-register-defect" data-work-order-id="${data.work_order_id}">불량 등록</button>
                        <button class="btn-info btn-view-lots" data-work-order-id="${data.work_order_id}">Lot 조회</button>
                        <button class="btn-secondary btn-view-bom" data-parent-stock-id="${data.stock_id}">BOM 조회</button>
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
    let table = `<table>
                    <thead>
                        <tr>
                            <th>BOM 코드</th>
                            <th>목표 재고코드</th>
                            <th>자재 재고코드</th>
                            <th>필요량</th>
                            <th>관리</th>
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
                            <td>${row.bom_id ?? '-'}</td>
                            <td>${row.parent_stock_id ?? '-'}</td>
                            <td>${row.child_stock_id ?? '-'}</td>
                            <td><strong>${numberFormat(row.required_qty)}</strong></td>
                            <td class="actions">-</td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="5" style="text-align:center;">BOM 정보가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;
//나중에 추가

    } catch (error) {
        console.error('BOM 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="5" style="text-align:center; color:red;">BOM 정보를 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 6. BOM 검색 폼
export function bom_search_form() {
    // main.html의 .search-form 디자인에 맞게 수정
    const search_bar = `
        <div class="form-group">
            <label for="parent_stock_id">목표 재고코드</label>
            <input type="text" id="parent_stock_id" name="parent_stock_id" placeholder="목표 재고코드 입력" />
        </div>
        <button type="submit" data-action="search" data-file="production" data-fn="bom_list">
            <i class="fas fa-search"></i> 검색
        </button>
    `;
    return search_bar;
}



// 7. BOM 조건 검색 (물품별)
export async function bom_list(formData) {
    const parent_stock_id = formData.parent_stock_id || '';

    let table = `<table>
                    <thead>
                        <tr>
                            <th>BOM 코드</th>
                            <th>목표 재고코드</th>
                            <th>자재 재고코드</th>
                            <th>필요량</th>
                            <th>관리</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: '/api/production/bom',
            method: 'GET',
            dataType: 'json',
            data: { parent_stock_id: parent_stock_id }
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                tbody += `<tr>
                            <td>${row.bom_id ?? '-'}</td>
                            <td>${row.parent_stock_id ?? '-'}</td>
                            <td>${row.child_stock_id ?? '-'}</td>
                            <td><strong>${numberFormat(row.required_qty)}</strong></td>
                            <td class="actions">-</td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="5" style="text-align:center;">검색 결과가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;
        // 나중에 추가

    } catch (error) {
        console.error('BOM 검색 실패:', error);
        tbody = `<tbody><tr><td colspan="5" style="text-align:center; color:red;">검색에 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}

// 9. Lot 추적 검색 폼
export function lot_tracking_search_form() {
    const search_bar = `<form data-file="production" data-fn="lot_tracking">
                            <label>Lot ID:
                                <input type="text" name="lot_id" placeholder="Lot ID 입력" required />
                            </label>
                            <button type="submit">추적</button>
                        </form>`;
    // 추후 이 폼도 .search-form 스타일로 수정이 필요
    return search_bar;
}



// 10. Lot 번호 추적 (품질 관리)
export async function lot_tracking(formData) {
    const lot_id = formData.lot_id || '';

    if (!lot_id) {
        return `<div class="error-message">Lot ID를 입력해주세요.</div>`;
    }

    let trackingHtml = '';

    try {
        const data = await $.ajax({
            url: '/api/production/lot_tracking',
            method: 'GET',
            dataType: 'json',
            data: { lot_id: lot_id }
        });

        trackingHtml = `
            <div class="lot-tracking-card">
                <h3>🔍 Lot 추적 정보</h3>
                
                <div class="tracking-section">
                    <h4>기본 정보</h4>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="label">Lot ID</span>
                            <span class="value highlight">${data.lot_id}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">품목번호</span>
                            <span class="value">${data.stock_id ?? '-'}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">Lot 생성일</span>
                            <span class="value">${formatDate(data.lot_date)}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">Lot 수량</span>
                            <span class="value">${numberFormat(data.lot_qty)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="tracking-section">
                    <h4>작업 지시서 정보</h4>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="label">지시서 번호</span>
                            <span class="value">
                                <button class="link-button" data-file="production" data-fn="work_order_detail" data-work-order-id="${data.work_order_id}">
                                    ${data.work_order_id}
                                </button>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="label">작업 상태</span>
                            <span class="value">${data.order_status ?? '-'}</span>
                        </div>
                    </div>
                </div>

                <div class="tracking-section">
                    <h4>원자재 BOM 정보</h4>
                    <div class="materials-list">`;

        if (data.bom && data.bom.length > 0) {
            $.each(data.bom, function(i, material) {
                trackingHtml += `
                    <div class="material-item">
                        <span class="material-code">📦 상위:${material.parent_stock_id} → 하위:${material.child_stock_id}</span>
                        <span class="material-quantity">소요량: ${numberFormat(material.required_qty)}</span>
                    </div>`;
            });
        } else {
            trackingHtml += `<p class="no-data">BOM 정보가 없습니다.</p>`;
        }

        trackingHtml += `
                    </div>
                </div>
                
                <div class="tracking-section">
                    <h4>불량 이력</h4>
                    <div class="defect-list">`;

        if (data.defects && data.defects.length > 0) {
            $.each(data.defects, function(i, defect) {
                trackingHtml += `
                    <div class="defect-item">
                        <span class="defect-name">⚠️ 코드 ${defect.defect_code}</span>
                        <span class="defect-qty">${numberFormat(defect.defect_qty)}개</span>
                    </div>`;
            });
        } else {
            trackingHtml += `<p class="no-defect">✅ 불량 내역이 없습니다.</p>`;
        }

        trackingHtml += `
                    </div>
                </div>
            </div>`;

    } catch (error) {
        console.error('Lot 추적 실패:', error);
        trackingHtml = `<div class="error-message">Lot 번호를 찾을 수 없거나 추적에 실패했습니다.</div>`;
    }

    return trackingHtml;
}


// 11. 작업지시서별 Lot 목록 조회
export async function work_order_lots(formData) {
    const work_order_id = formData.work_order_id || '';

    if (!work_order_id) {
        return `<div class="error-message">작업지시서 번호가 필요합니다.</div>`;
    }

    let table = `<table class="lot-table">
                    <thead>
                        <tr>
                            <th>Lot ID</th>
                            <th>품목번호</th>
                            <th>Lot 생성일</th>
                            <th>Lot 수량</th>
                            <th>추적</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: `/api/production/work_order/${work_order_id}/lots`,
            method: 'GET',
            dataType: 'json'
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                tbody += `<tr>
                            <td>${row.lot_id}</td>
                            <td>${row.stock_id ?? '-'}</td>
                            <td>${formatDate(row.lot_date)}</td>
                            <td>${numberFormat(row.lot_qty)}</td>
                            <td>
                                <button class="btn-track" data-file="production" data-fn="lot_tracking" data-lot-id="${row.lot_id}">추적</button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="5" style="text-align:center;">등록된 Lot이 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('Lot 목록 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="5" style="text-align:center; color:red;">Lot 목록을 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 12. 작업지시서별 불량 내역 조회
export async function work_order_defects(formData) {
    const work_order_id = formData.work_order_id || '';

    if (!work_order_id) {
        return `<div class="error-message">작업지시서 번호가 필요합니다.</div>`;
    }

    let table = `<table class="defect-table">
                    <thead>
                        <tr>
                            <th>불량코드</th>
                            <th>Lot ID</th>
                            <th>품목번호</th>
                            <th>불량 수량</th>
                            <th>등록일</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: `/api/production/work_order/${work_order_id}/defects`,
            method: 'GET',
            dataType: 'json'
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            let totalDefect = 0;
            $.each(data, function(i, row) {
                totalDefect += parseInt(row.defect_qty) || 0;
                tbody += `<tr>
                            <td>${row.defect_code}</td>
                            <td>${row.lot_id ?? '-'}</td>
                            <td>${row.stock_id ?? '-'}</td>
                            <td class="defect-qty">${numberFormat(row.defect_qty)}</td>
                            <td>${formatDate(row.defect_date)}</td>
                          </tr>`;
            });

            tbody += `<tr class="total-row">
                        <td colspan="3"><strong>합계</strong></td>
                        <td class="defect-qty"><strong>${numberFormat(totalDefect)}</strong></td>
                        <td></td>
                      </tr>`;
        } else {
            tbody += `<tr><td colspan="5" style="text-align:center;">불량 내역이 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('불량 내역 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="5" style="text-align:center; color:red;">불량 내역을 불러오는데 실패했습니다.</td></tr></tbody></table>`;
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

function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
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

export async function work_order_detail_popup(e) {

    const work_order_id = e.dataset.workOrderId;

    if (!work_order_id) {
        alert('상세 정보를 불러올 수 없습니다. (work_order_id 누락)');
        return;
    }

    // 2. 팝업 URL과 속성을 정의
    const url = `./../popup/detailWorkOrder.html?work_order_id=${work_order_id}`;
    const features = 'width=700,height=600,resizable=yes,scrollbars=yes'; // 팝업 크기 조정

    // 3. 새 팝업 창
    window.open(url, 'detailWorkOrder', features).focus();
}

// 작업지시서 추가
export function addWorkOrder(){
    const url='./../popup/addWorkOrder.html';
    const features = 'width=600,height=450,resizable=no,scrollbars=yes';
    window.open(url,'add_WorkOrder', features).focus();
}