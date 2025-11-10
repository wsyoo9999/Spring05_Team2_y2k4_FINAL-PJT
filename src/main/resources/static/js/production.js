// 1. 작업 지시서 전체 목록 조회
export async function work_order_listAll() {
    let table = `<table>
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
                // const statusClass = getStatusClass(row.order_status); // (추후 CSS 구현 시)

                tbody += `<tr data-order-id="${row.order_id}">
                            <td><strong>${row.order_id}</strong></td>
                            <td>${row.item_id}</td>
                            <td>${formatDate(row.start_date)}</td>
                            <td>${formatDate(row.due_date)}</td>
                            <td>${numberFormat(row.target_quantity)}</td>
                            <td class="good-qty">${numberFormat(row.good_quantity)}</td>
                            <td class="defect-qty">${numberFormat(row.defect_quantity)}</td>
                            <td>${row.order_status}</td>
                            <td class="actions">
                                <button class="btn-detail" 
                                        data-action="detail" 
                                        data-file="production" 
                                        data-fn="work_order_detail_popup" 
                                        data-order-id="${row.order_id}"
                                        title="상세 보기">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="9" style="text-align:center;">조회된 작업 지시서가 없습니다.</td></tr>`;
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
        tbody = `<tbody><tr><td colspan="9" style="text-align:center; color:red;">데이터를 불러오는데 실패했습니다.</td></tr></tbody></table>`;
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
            <label for="item_id">물품번호</label>
            <input type="text" id="item_id" name="item_id" placeholder="물품번호 입력" />
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
    const item_id = formData.item_id || '';
    const start_date = formData.start_date || '';
    const due_date = formData.due_date || '';

    let table = `<table>
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
                // const statusClass = getStatusClass(row.order_status);

                tbody += `<tr data-order-id="${row.order_id}">
                            <td><strong>${row.order_id}</strong></td>
                            <td>${row.item_id}</td>
                            <td>${formatDate(row.start_date)}</td>
                            <td>${formatDate(row.due_date)}</td>
                            <td>${numberFormat(row.target_quantity)}</td>
                            <td class="good-qty">${numberFormat(row.good_quantity)}</td>
                            <td class="defect-qty">${numberFormat(row.defect_quantity)}</td>
                            <td>${row.order_status}</td>
                            <td class="actions">
                                <button class="btn-detail"
                                        data-action="detail"
                                        data-file="production"
                                        data-fn="work_order_detail_popup"
                                        data-order-id="${row.order_id}"
                                        title="상세 보기">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="9" style="text-align:center;">검색 결과가 없습니다.</td></tr>`;
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
        tbody = `<tbody><tr><td colspan="9" style="text-align:center; color:red;">검색에 실패했습니다.</td></tr></tbody></table>`;
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
    let table = `<table>
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
            tbody += `<tr><td colspan="4" style="text-align:center;">BOM 정보가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;
//나중에 추가


    } catch (error) {
        console.error('BOM 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="4" style="text-align:center; color:red;">BOM 정보를 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 6. BOM 검색 폼
export function bom_search_form() {
    // main.html의 .search-form 디자인에 맞게 수정
    const search_bar = `
        <div class="form-group">
            <label for="bom_item_id">물품번호</label>
            <input type="text" id="bom_item_id" name="item_id" placeholder="물품번호 입력" />
        </div>
        <button type="submit" data-action="search" data-file="production" data-fn="bom_list">
            <i class="fas fa-search"></i> 검색
        </button>
    `;
    return search_bar;
}



// 7. BOM 조건 검색 (물품별)
export async function bom_list(formData) {
    const item_id = formData.item_id || '';

    let table = `<table>
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
            tbody += `<tr><td colspan="4" style="text-align:center;">검색 결과가 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;
        // 나중에 추가

    } catch (error) {
        console.error('BOM 검색 실패:', error);
        tbody = `<tbody><tr><td colspan="4" style="text-align:center; color:red;">검색에 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}

// 9. Lot 추적 검색 폼
export function lot_tracking_search_form() {
    const search_bar = `<form data-file="production" data-fn="lot_tracking">
                            <label>Lot 번호:
                                <input type="text" name="lot_number" placeholder="Lot 번호 입력" required />
                            </label>
                            <button type="submit">추적</button>
                        </form>`;
    // 추후 이 폼도 .search-form 스타일로 수정이 필요
    return search_bar;
}



// 10. Lot 번호 추적 (품질 관리)
export async function lot_tracking(formData) {
    const lot_number = formData.lot_number || '';

    if (!lot_number) {
        return `<div class="error-message">Lot 번호를 입력해주세요.</div>`;
    }

    let trackingHtml = '';

    try {
        const data = await $.ajax({
            url: '/api/production/lot_tracking',
            method: 'GET',
            dataType: 'json',
            data: { lot_number: lot_number }
        });

        trackingHtml = `
            <div class="lot-tracking-card">
                <h3>🔍 Lot 추적 정보</h3>
                
                <div class="tracking-section">
                    <h4>기본 정보</h4>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="label">Lot 번호</span>
                            <span class="value highlight">${data.lot_number}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">물품번호</span>
                            <span class="value">${data.item_id}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">제조날짜</span>
                            <span class="value">${formatDate(data.production_date)}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">생산 수량</span>
                            <span class="value">${numberFormat(data.lot_quantity)}</span>
                        </div>
                    </div>
                </div>
                
                <div class="tracking-section">
                    <h4>작업 지시서 정보</h4>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="label">지시서 번호</span>
                            <span class="value">
                                <button class="link-button" data-file="production" data-fn="work_order_detail" data-order-id="${data.order_id}">
                                    ${data.order_id}
                                </button>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="label">작업 상태</span>
                            <span class="value">${data.order_status}</span>
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
                        <span class="material-code">📦 ${material.raw_materials_code}</span>
                        <span class="material-quantity">소요량: ${numberFormat(material.required_quantity)}</span>
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
                        <span class="defect-name">⚠️ ${defect.defect_name}</span>
                        <span class="defect-qty">${numberFormat(defect.defect_quantity)}개</span>
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
    const order_id = formData.order_id || '';

    if (!order_id) {
        return `<div class="error-message">작업지시서 번호가 필요합니다.</div>`;
    }

    let table = `<table class="lot-table">
                    <thead>
                        <tr>
                            <th>Lot ID</th>
                            <th>Lot번호</th>
                            <th>물품번호</th>
                            <th>제조날짜</th>
                            <th>Lot수량</th>
                            <th>추적</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: `/api/production/work_order/${order_id}/lots`,
            method: 'GET',
            dataType: 'json'
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            $.each(data, function(i, row) {
                tbody += `<tr>
                            <td>${row.lot_id}</td>
                            <td><strong>${row.lot_number}</strong></td>
                            <td>${row.item_id}</td>
                            <td>${formatDate(row.production_date)}</td>
                            <td>${numberFormat(row.lot_quantity)}</td>
                            <td>
                                <button class="btn-track" data-file="production" data-fn="lot_tracking" data-lot-number="${row.lot_number}">추적</button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += `<tr><td colspan="6" style="text-align:center;">등록된 Lot이 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('Lot 목록 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="6" style="text-align:center; color:red;">Lot 목록을 불러오는데 실패했습니다.</td></tr></tbody></table>`;
    }

    return table + tbody;
}


// 12. 작업지시서별 불량 내역 조회
export async function work_order_defects(formData) {
    const order_id = formData.order_id || '';

    if (!order_id) {
        return `<div class="error-message">작업지시서 번호가 필요합니다.</div>`;
    }

    let table = `<table class="defect-table">
                    <thead>
                        <tr>
                            <th>불량코드</th>
                            <th>불량명</th>
                            <th>불량 수량</th>
                            <th>등록일</th>
                        </tr>
                    </thead>`;

    let tbody = '';

    try {
        const data = await $.ajax({
            url: `/api/production/work_order/${order_id}/defects`,
            method: 'GET',
            dataType: 'json'
        });

        tbody += `<tbody>`;

        if (data && data.length > 0) {
            let totalDefect = 0;
            $.each(data, function(i, row) {
                totalDefect += parseInt(row.defect_quantity) || 0;
                tbody += `<tr>
                            <td>${row.defect_id}</td>
                            <td>${row.defect_name}</td>
                            <td class="defect-qty">${numberFormat(row.defect_quantity)}</td>
                            <td>${formatDate(row.detected_date)}</td>
                          </tr>`;
            });

            tbody += `<tr class="total-row">
                        <td colspan="2"><strong>합계</strong></td>
                        <td class="defect-qty"><strong>${numberFormat(totalDefect)}</strong></td>
                        <td></td>
                      </tr>`;
        } else {
            tbody += `<tr><td colspan="4" style="text-align:center;">불량 내역이 없습니다.</td></tr>`;
        }

        tbody += `</tbody></table>`;

    } catch (error) {
        console.error('불량 내역 조회 실패:', error);
        tbody = `<tbody><tr><td colspan="4" style="text-align:center; color:red;">불량 내역을 불러오는데 실패했습니다.</td></tr></tbody></table>`;
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

export async function work_order_detail_popup(e) {

    const order_id = e.dataset.orderId;

    if (!order_id) {
        alert('상세 정보를 불러올 수 없습니다. (order_id 누락)');
        return;
    }

    // 2. 팝업 URL과 속성을 정의
    const url = `./../popup/detailWorkOrder.html?order_id=${order_id}`;
    const features = 'width=700,height=600,resizable=yes,scrollbars=yes'; // 팝업 크기 조정

    // 3. 새 팝업 창
    window.open(url, 'detailWorkOrder', features).focus();
}

// 작업지시서 추가
export function addWorkOrder(){
    const url='./../popup/addWorkOrder.html';
    const features = 'width=600,height=480,resizable=no,scrollbars=yes';
    window.open(url,'add_WorkOrder', features).focus();
}