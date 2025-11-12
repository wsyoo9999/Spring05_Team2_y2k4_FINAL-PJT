/**
 * static/js/finance.js
 * 재무/회계 API (결재 문서, 회사 수익, 회사 지출) 호출 및 HTML 생성
 */

const API_BASE_URL = '/api/finance';

// ================================================================
// 0. 유틸리티 함수
// ================================================================

// Date 객체 또는 [년, 월, 일] 배열에서 'YYYY-MM-DD' 형식의 날짜 문자열을 반환
function formatDateTime(dateString) {
    if (!dateString) return '-';
    // Spring의 LocalDateTime/LocalDate 처리
    let date;
    if (Array.isArray(dateString) && dateString.length >= 3) {
        // [year, month, day, hour, minute, second] 형태 처리
        date = new Date(dateString[0], dateString[1] - 1, dateString[2]);
    } else {
        try {
            date = new Date(dateString);
        } catch (e) {
            return dateString; // 변환 실패 시 원본 반환
        }
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}


// ================================================================
// 1. 결재 문서 관리 (Documents) - API 1~6번
// ================================================================

// 결재 문서 검색 폼
export function documents_search_form() {
    const search_bar = `
    <form data-file="finance" data-fn="documents_list">
        <div class="form-group">
            <label for="doc_status">상태</label>
            <select id="doc_status" name="status">
                <option value="">전체</option>
                <option value="PENDING">대기</option>
                <option value="APPROVED">승인</option>
                <option value="REJECTED">반려</option>
            </select>
        </div>
        <div class="form-group">
            <label for="requesterId">기안자 ID</label>
            <input type="number" id="requesterId" name="requesterId" placeholder="기안자 ID" />
        </div>
        <input type="submit" value="🔍 검색" data-action="search" data-file="finance" data-fn="documents_list" class="search_btn" />
    </form>
    `;
    return search_bar;
}

// 결재 문서 목록 전체 조회
export async function documents_listAll() {
    return await documents_fetch_data({});
}

// 결재 문서 목록 조건 검색
export async function documents_list(formData) {
    return await documents_fetch_data(formData);
}

// 결재 문서 데이터 AJAX 호출 및 HTML 생성 공통 함수 (API 5번)
async function documents_fetch_data(formData) {
    const requesterId = formData.requesterId || '';
    const status = formData.status || '';

    const actionRow = `
        <div class="table-actions-header">
            <button class="action-button btn-primary" data-action="add" data-file="finance" data-fn="registerDocument">
                <i class="fas fa-plus-circle"></i> 결재 문서 등록
            </button>
        </div>
    `;

    let table = `<table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>제목</th>
                            <th>기안자ID</th>
                            <th>기안일</th>
                            <th>상태</th>
                            <th>결재자ID</th>
                            <th style="text-align: center;">기능</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    const requestData = {};
    if (requesterId) requestData.requesterId = requesterId;
    if (status) requestData.status = status;

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/documents`,
            method: 'GET',
            dataType: 'json',
            data: requestData
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                tbody += `<tr>
                            <td>${row.docId}</td>
                            <td>
                                <a href="#" data-action="detail" data-file="finance" data-fn="getDocument" data-id="${row.docId}">
                                    <strong>${row.title || ''}</strong>
                                </a>
                            </td>
                            <td>${row.requesterId || ''}</td>
                            <td>${formatDateTime(row.requestDate) || ''}</td>
                            <td><strong>${row.status || ''}</strong></td>
                            <td>${row.approverId || '-'}</td>
                            <td class="actions">
                                <button data-action="detail" data-file="finance" data-fn="getDocument" data-id="${row.docId}" title="상세">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                                <button data-action="delete" data-file="finance" data-fn="deleteDocument" data-id="${row.docId}" title="삭제">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="7" style="text-align:center;">결재 문서가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;

        return actionRow + table + tbody;

    } catch (err) {
        console.error("documents_list 로딩 실패:", err);
        return actionRow + table + `<tbody><tr><td colspan="7" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}

// ================================================================
// 2. 회사 수익 관리 (Profit CRUD) - API 7번 대역
// ================================================================

// 수익 목록 검색 폼
export function profit_search_form() {
    return `
    <form data-file="finance" data-fn="profit_list">
        <div class="form-group">
            <label for="profitCode">수익 코드</label>
            <input type="number" id="profitCode" name="profitCode" placeholder="수익 원인 코드" />
        </div>
        <div class="form-group">
            <label for="searchComment">비고 검색</label>
            <input type="text" id="searchComment" name="searchComment" placeholder="비고 검색 키워드" />
        </div>
        <input type="submit" value="🔍 검색" data-action="search" data-file="finance" data-fn="profit_list" class="search_btn" />
    </form>
    `;
}

// 수익 목록 전체 조회 (API 7-2번)
export async function profit_listAll() {
    return await profit_fetch_data({});
}

// 수익 목록 조건 검색
export async function profit_list(formData) {
    return await profit_fetch_data(formData);
}

// 수익 데이터 AJAX 호출 및 HTML 생성 공통 함수 (API 7-2번)
async function profit_fetch_data(formData) {
    const profitCode = formData.profitCode || null;
    const searchComment = formData.searchComment || null;

    const actionRow = `
        <div class="table-actions-header">
            <button class="action-button btn-primary" data-action="add" data-file="finance" data-fn="registerProfit">
                <i class="fas fa-plus-circle"></i> 수익 등록
            </button>
        </div>
    `;

    let table = `<table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>수익 코드</th>
                            <th>수익 금액</th>
                            <th>수익 일자</th>
                            <th>비고</th>
                            <th style="text-align: center;">기능</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    const requestData = {};
    if (profitCode) requestData.profitCode = profitCode;
    if (searchComment) requestData.searchComment = searchComment;

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/profit`,
            method: 'GET',
            dataType: 'json',
            data: requestData
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                tbody += `<tr>
                            <td>
                                <a href="#" data-action="detail" data-file="finance" data-fn="getProfit" data-id="${row.profitId}">
                                    <strong>${row.profitId}</strong>
                                </a>
                            </td>
                            <td>${row.profitCode || '-'}</td>
                            <td style="font-weight: bold; color: green;">${row.profit ? row.profit.toLocaleString() : '0'} 원</td>
                            <td>${formatDateTime(row.profitDate)}</td>
                            <td>${row.profitComment || '-'}</td>
                            <td class="actions">
                                <button data-action="detail" data-file="finance" data-fn="getProfit" data-id="${row.profitId}" title="상세/수정">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                                <button data-action="delete" data-file="finance" data-fn="deleteProfit" data-id="${row.profitId}" title="삭제">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="6" style="text-align:center;">수익 데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return actionRow + table + tbody;

    } catch (err) {
        console.error("profit_list 로딩 실패:", err);
        return actionRow + table + `<tbody><tr><td colspan="6" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}


// ================================================================
// 3. 회사 지출 관리 (Spend CRUD) - API 8번 대역
// ================================================================

// 지출 목록 검색 폼
export function spend_search_form() {
    return `
    <form data-file="finance" data-fn="spend_list">
        <div class="form-group">
            <label for="spendCode">지출 코드</label>
            <input type="number" id="spendCode" name="spendCode" placeholder="지출 원인 코드" />
        </div>
        <div class="form-group">
            <label for="searchComment">비고 검색</label>
            <input type="text" id="searchComment" name="searchComment" placeholder="비고 검색 키워드" />
        </div>
        <input type="submit" value="🔍 검색" data-action="search" data-file="finance" data-fn="spend_list" class="search_btn" />
    </form>
    `;
}

// 지출 목록 전체 조회 (API 8-2번)
export async function spend_listAll() {
    return await spend_fetch_data({});
}

// 지출 목록 조건 검색
export async function spend_list(formData) {
    return await spend_fetch_data(formData);
}

// 지출 데이터 AJAX 호출 및 HTML 생성 공통 함수 (API 8-2번)
async function spend_fetch_data(formData) {
    const spendCode = formData.spendCode || null;
    const searchComment = formData.searchComment || null;

    const actionRow = `
        <div class="table-actions-header">
            <button class="action-button btn-primary" data-action="add" data-file="finance" data-fn="registerSpend">
                <i class="fas fa-plus-circle"></i> 지출 등록
            </button>
        </div>
    `;

    let table = `<table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>지출 코드</th>
                            <th>지출 금액</th>
                            <th>지출 일자</th>
                            <th>비고</th>
                            <th style="text-align: center;">기능</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    const requestData = {};
    if (spendCode) requestData.spendCode = spendCode;
    if (searchComment) requestData.searchComment = searchComment;

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/spend`,
            method: 'GET',
            dataType: 'json',
            data: requestData
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                tbody += `<tr>
                            <td>
                                <a href="#" data-action="detail" data-file="finance" data-fn="getSpend" data-id="${row.spendId}">
                                    <strong>${row.spendId}</strong>
                                </a>
                            </td>
                            <td>${row.spendCode || '-'}</td>
                            <td style="font-weight: bold; color: red;">${row.spend ? row.spend.toLocaleString() : '0'} 원</td>
                            <td>${formatDateTime(row.spendDate)}</td>
                            <td>${row.spendComment || '-'}</td>
                            <td class="actions">
                                <button data-action="detail" data-file="finance" data-fn="getSpend" data-id="${row.spendId}" title="상세/수정">
                                    <i class="fas fa-info-circle"></i>
                                </button>
                                <button data-action="delete" data-file="finance" data-fn="deleteSpend" data-id="${row.spendId}" title="삭제">
                                    <i class="fas fa-trash"></i>
                                </button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="6" style="text-align:center;">지출 데이터가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return actionRow + table + tbody;

    } catch (err) {
        console.error("spend_list 로딩 실패:", err);
        return actionRow + table + `<tbody><tr><td colspan="6" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}


// ================================================================
// 4. 기타 CRUD 및 액션 함수 (팝업 연결 및 삭제 로직)
// ================================================================

// --- Documents 관련 함수 (API 1, 3, 4) ---

// API 1. 결재 문서 등록 (팝업)
export function registerDocument() {
    const url = './../popup/documents_register.html';
    const features = 'width=700,height=650,left=100,top=100';
    window.open(url, 'RegisterDocument', features).focus();
}

// API 3. 결재 문서 삭제 (실제 API 호출)
export async function deleteDocument(e) {
    const docId = e.dataset.id;
    if (!confirm(`정말로 문서 ID ${docId}를 삭제하시겠습니까?`)) {
        return;
    }

    try {
        await $.ajax({
            url: `${API_BASE_URL}/documents/${docId}`,
            method: 'DELETE'
        });
        alert(`✅ 문서 ID ${docId} 삭제 완료.`);
        // 목록 새로고침
        document.querySelector('.menu[data-fn="documents_listAll"]').click();
    } catch (error) {
        console.error('문서 삭제 실패:', error);
        alert('❌ 문서 삭제에 실패했습니다. (권한 또는 서버 오류)');
    }
}

// API 4. 결재 문서 상세 조회 (팝업)
export function getDocument(e) {
    const docId = e.dataset.id;
    const url = `./../popup/documents_detail.html?docId=${docId}`;
    const features = 'width=700,height=800,left=100,top=100';
    window.open(url, 'DocumentDetail', features).focus();
}

// --- Profit 관련 함수 (API 7-1, 7-3, 7-5) ---

// 7-1. 수익 등록 (POST) 팝업 연결
export function registerProfit() {
    window.open('./popup/profit_register.html', 'RegisterProfit', 'width=550,height=500,left=100,top=100').focus();
}

// 7-3. 수익 상세/수정 (GET/PUT) 팝업 연결
export function getProfit(e) {
    const profitId = e.dataset.id;
    window.open(`./popup/profit_detail.html?profitId=${profitId}`, 'ProfitDetail', 'width=550,height=550,left=100,top=100').focus();
}

// 7-5. 수익 삭제 (DELETE)
export async function deleteProfit(e) {
    const profitId = e.dataset.id;
    if (!confirm(`수익 ID ${profitId}를 삭제하시겠습니까?`)) return;

    try {
        await $.ajax({ url: `${API_BASE_URL}/profit/${profitId}`, method: 'DELETE' });
        alert(`✅ 수익 ID ${profitId} 삭제 완료.`);
        // 목록 새로고침
        document.querySelector('.menu[data-fn="profit_listAll"]').click();
    } catch (error) {
        console.error('수익 삭제 실패:', error);
        alert('❌ 수익 삭제에 실패했습니다.');
    }
}

// --- Spend 관련 함수 (API 8-1, 8-3, 8-5) ---

// 8-1. 지출 등록 (POST) 팝업 연결
export function registerSpend() {
    window.open('./popup/spend_register.html', 'RegisterSpend', 'width=550,height=500,left=100,top=100').focus();
}

// 8-3. 지출 상세/수정 (GET/PUT) 팝업 연결
export function getSpend(e) {
    const spendId = e.dataset.id;
    window.open(`./popup/spend_detail.html?spendId=${spendId}`, 'SpendDetail', 'width=550,height=550,left=100,top=100').focus();
}

// 8-5. 지출 삭제 (DELETE)
export async function deleteSpend(e) {
    const spendId = e.dataset.id;
    if (!confirm(`지출 ID ${spendId}를 삭제하시겠습니까?`)) return;

    try {
        await $.ajax({ url: `${API_BASE_URL}/spend/${spendId}`, method: 'DELETE' });
        alert(`✅ 지출 ID ${spendId} 삭제 완료.`);
        // 목록 새로고침
        document.querySelector('.menu[data-fn="spend_listAll"]').click();
    } catch (error) {
        console.error('지출 삭제 실패:', error);
        alert('❌ 지출 삭제에 실패했습니다.');
    }
}

// --- 기타 더미 함수 ---
// *주의: updateDocument는 getDocument 팝업 내에서 처리되므로 별도의 팝업 함수로서는 사용되지 않습니다.

// API 2. 결재 문서 수정 (더미) - 팝업 내에서 처리됨.
export function updateDocument(e) {
    const docId = e.dataset.id;
    alert(`결재 문서 ID ${docId} 수정 기능은 상세 팝업에서 처리됩니다.`);
}