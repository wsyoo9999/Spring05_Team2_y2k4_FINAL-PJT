/**
 * static/js/finance.js
 * 재무/회계 API (결재 문서, 예산, 전표) 호출 및 HTML 생성
 */

const API_BASE_URL = '/api/finance';

// ================================================================
// 1. 결재 문서 관리 (Documents)
// ================================================================

// 결재 문서 검색 폼
export function documents_search_form() {
    const search_bar = `<form data-file="finance" data-fn="documents_list">
                            <label>상태:
                                <select name="status">
                                    <option value="">전체</option>
                                    <option value="PENDING">대기</option>
                                    <option value="APPROVED">승인</option>
                                    <option value="REJECTED">반려</option>
                                </select>
                            </label>
                            <label>기안자 ID:
                                <input type="number" name="requesterId" placeholder="기안자 ID" />
                            </label>
                            <button type="submit" data-action="search" class="search_btn">검색</button>
                        </form>`;
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
    const requesterId = formData.requesterId;
    const status = formData.status;

    let table = `<div style="text-align:right; margin-bottom:10px;">
                    <button data-action="add" data-file="finance" data-fn="registerDocument">문서 등록</button>
                 </div>
                 <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>제목</th>
                            <th>기안자ID</th>
                            <th>기안일</th>
                            <th>상태</th>
                            <th>결재자ID</th>
                            <th>기능</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    // 🌟 서버로 보낼 데이터 객체 구성
    const requestData = {};
    if (requesterId && requesterId !== '') { // requesterId가 있고 빈 문자열이 아닐 때만 추가
        requestData.requesterId = requesterId;
    }
    if (status && status !== '') { // status가 있고 빈 문자열이 아닐 때만 추가
        requestData.status = status;
    }
    // 🌟 빈 객체일 경우, 파라미터 없이 GET 요청이 전송됨

    try {
        const data = await $.ajax({
            url: `${API_BASE_URL}/documents`,
            method: 'GET',
            dataType: 'json',
            data: requestData // 👈 파라미터가 있는 경우에만 전송
        });

        if (data && data.length > 0) {
            $.each(data, function (i, row) {
                tbody += `<tr>
                            <td>${row.docId}</td>
                            <td><a href="#" data-action="detail" data-file="finance" data-fn="getDocument" data-id="${row.docId}"><strong>${row.title || ''}</strong></a></td>
                            <td>${row.requesterId || ''}</td>
                            <td>${row.requestDate || ''}</td>
                            <td><strong>${row.status || ''}</strong></td>
                            <td>${row.approverId || '-'}</td>
                            <td>
                                <button data-action="detail" data-file="finance" data-fn="getDocument" data-id="${row.docId}">상세/수정</button>
                                <button data-action="delete" data-file="finance" data-fn="deleteDocument" data-id="${row.docId}">삭제</button>
                            </td>
                          </tr>`;
            });
        } else {
            tbody += '<tr><td colspan="7" style="text-align:center;">결재 문서가 없습니다.</td></tr>';
        }
        tbody += `</tbody></table>`;
        return table + tbody;
    } catch (err) {
        console.error("documents_list 로딩 실패:", err);
        return table + `<tbody><tr><td colspan="7" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}


// --- 2. 예산 관리 (Budget) ---

// 예산 계정 검색 폼 (코드별 조회)
export function budget_search_form() {
    const search_bar = `<form data-file="finance" data-fn="budget_list">
                            <label>계정 코드:
                                <input type="text" name="acctCode" placeholder="예산 계정 코드 (예: 401)" />
                            </label>
                            <button type="submit" data-action="search" class="search_btn">검색</button>
                        </form>`;
    return search_bar;
}

// 예산 계정 전체/조건 조회 (API 7번 기반)
export function budget_listAll() {
    // 예산 목록 조회 API가 없으므로 더미 메시지 반환
    return Promise.resolve("<h3>예산 관리</h3><p>전체 계정 목록 조회 기능은 API 구현이 필요합니다. 상세 검색 또는 '잔액 확인'을 이용해 주세요.</p>");
}

export async function budget_list(formData) {
    const acctCode = formData.acctCode;
    if (!acctCode) {
        return budget_listAll();
    }

    let table = `<table>
                    <thead>
                        <tr>
                            <th>계정 코드</th>
                            <th>계정 이름</th>
                            <th>연간 예산</th>
                            <th>잔액</th>
                            <th>기능</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    try {
        // API 7번: 예산 계정 조회
        const row = await $.ajax({
            url: `${API_BASE_URL}/budget/accounts/${acctCode}`,
            method: 'GET',
            dataType: 'json'
        });

        if (row && row.acctCode) {
            tbody += `<tr>
                        <td>${row.acctCode}</td>
                        <td>${row.acctName || ''}</td>
                        <td>${row.annualBudget ? row.annualBudget.toLocaleString() : '0'}</td>
                        <td><strong>${row.remains ? row.remains.toLocaleString() : '0'}</strong></td>
                        <td>
                           <button data-action="check" data-file="finance" data-fn="checkBudget" data-acct="${row.acctCode}">잔액 확인/업데이트</button>
                        </td>
                      </tr>`;
        } else {
            tbody += `<tr><td colspan="5" style="text-align:center;">계정 코드가 ${acctCode}인 데이터가 없습니다.</td></tr>`;
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        console.error("budget_list 로딩 실패:", err);
        return table + `<tbody><tr><td colspan="5" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}


// --- 3. 회계 전표 관리 (Slips) ---

// 회계 전표 검색 폼 (ID별 조회)
export function slips_search_form() {
    const search_bar = `<form data-file="finance" data-fn="slips_list">
                            <label>전표 ID:
                                <input type="number" name="slipId" placeholder="전표 ID 입력" />
                            </label>
                            <button type="submit" data-action="search" class="search_btn">검색</button>
                        </form>`;
    return search_bar;
}

// 회계 전표 전체/조건 조회 (API 11번 기반)
export function slips_listAll() {
    return Promise.resolve("<h3>회계 전표 상세 조회</h3><p>전표 목록 조회 기능은 API 구현이 필요합니다. 전표 ID를 입력하여 상세 조회해 주세요.</p>");
}

export async function slips_list(formData) {
    const slipId = formData.slipId;
    if (!slipId) {
        return slips_listAll();
    }

    let table = `<table>
                    <thead>
                        <tr>
                            <th>전표 ID</th>
                            <th>문서 ID</th>
                            <th>계정 코드/이름</th>
                            <th>차변 금액</th>
                            <th>대변 금액</th>
                            <th>전송 상태</th>
                            <th>기능</th>
                        </tr>
                    </thead>`;
    let tbody = '<tbody>';

    try {
        // API 11번: 회계 전표 상세 조회
        const row = await $.ajax({
            url: `${API_BASE_URL}/slips/${slipId}`,
            method: 'GET',
            dataType: 'json'
        });

        if (row && row.slipId) {
            tbody += `<tr>
                        <td>${row.slipId}</td>
                        <td>${row.docId || '-'}</td>
                        <td>${row.acctCode || '-'}/${row.acctName || '-'}</td>
                        <td>${row.debitAmount ? row.debitAmount.toLocaleString() : '0'}</td>
                        <td>${row.creditAmount ? row.creditAmount.toLocaleString() : '0'}</td>
                        <td><strong>${row.transferStatus || '-'}</strong></td>
                        <td>
                            <button data-action="detail" data-file="finance" data-fn="getSlip" data-id="${row.slipId}">상세</button>
                        </td>
                      </tr>`;
        } else {
            tbody += `<tr><td colspan="7" style="text-align:center;">전표 ID ${slipId}인 데이터가 없습니다.</td></tr>`;
        }
        tbody += `</tbody></table>`;
        return table + tbody;

    } catch (err) {
        console.error("slips_list 로딩 실패:", err);
        return table + `<tbody><tr><td colspan="7" style="text-align:center; color:red;">데이터 로딩 실패</td></tr></tbody></table>`;
    }
}


// --- 4. 팝업 실행 함수 (finance.js에서 직접 호출) ---

// API 1. 결재 문서 등록 (팝업)
export function registerDocument(e) {
    window.open('./popup/documents_register.html', 'RegisterDocument', 'width=700,height=650,left=100,top=100');
}

// API 4. 결재 문서 상세 조회 (팝업)
export function getDocument(e) {
    const docId = e.dataset.id;
    window.open(`./popup/documents_detail.html?docId=${docId}`, 'DocumentDetail', 'width=700,height=800,left=100,top=100');
}

// API 11. 회계 전표 상세 조회 (팝업)
export function getSlip(e) {
    const slipId = e.dataset.id;
    window.open(`./popup/slips_detail.html?slipId=${slipId}`, 'SlipDetail', 'width=600,height=550,left=150,top=150');
}

// API 8 & 9. 예산 잔액 확인/업데이트 (팝업)
export function checkBudget(e) {
    const acctCode = e.dataset.acct;
    window.open(`./popup/budget_check.html?acctCode=${acctCode}`, 'BudgetCheck', 'width=550,height=500,left=200,top=200');
}

// API 3. 결재 문서 삭제 (더미)
export function deleteDocument(e) {
    const docId = e.dataset.id;
    if(confirm(`정말로 문서 ID ${docId}를 삭제하시겠습니까?`)) {
        // 실제 DELETE API 호출 로직 추가 필요
        alert(`문서 ID ${docId} 삭제 API 호출 예정`);
    }
}

// --- 기타 CRUD 함수 (나머지 12가지 API는 주로 팝업 내에서 처리됨) ---