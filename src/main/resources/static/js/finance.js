// ----------------------------------------------------
// 1. 결재 문서 등록 (POST, /api/finance/documents)
// ----------------------------------------------------
export async function registerDocument(docData) {
    try {
        const result = await $.ajax({
            url: '/api/finance/documents',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(docData) // JS 객체를 JSON 문자열로 변환하여 전송
        });
        console.log('결재 문서 등록 성공:', result);
        return result;
    } catch (error) {
        console.error('결재 문서 등록 실패:', error);
        throw error;
    }
}

// ----------------------------------------------------
// 5. 결재 문서 목록 조회 (GET, /api/finance/documents)
// ----------------------------------------------------
export async function listDocuments() {
    try {
        const data = await $.ajax({
            url: '/api/finance/documents',
            method: 'GET',
            dataType: 'json'
        });
        console.log('결재 문서 목록:', data);
        // 데이터를 받아 HTML 테이블 등으로 가공하는 로직 추가 가능
        return data;
    } catch (error) {
        console.error('결재 문서 목록 조회 실패:', error);
        throw error;
    }
}

// ----------------------------------------------------
// 4. 결재 문서 상세 조회 (GET, /api/finance/documents/{docId})
// ----------------------------------------------------
export async function getDocument(docId) {
    try {
        const data = await $.ajax({
            url: `/api/finance/documents/${docId}`,
            method: 'GET',
            dataType: 'json'
        });
        console.log(`문서 ID ${docId} 상세 조회:`, data);
        return data;
    } catch (error) {
        console.error(`문서 ID ${docId} 상세 조회 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 2. 결재 문서 수정 (PUT, /api/finance/documents/{docId})
// ----------------------------------------------------
export async function updateDocument(docId, docData) {
    try {
        const result = await $.ajax({
            url: `/api/finance/documents/${docId}`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(docData)
        });
        console.log(`문서 ID ${docId} 수정 성공:`, result);
        return result;
    } catch (error) {
        console.error(`문서 ID ${docId} 수정 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 3. 결재 문서 삭제 (DELETE, /api/finance/documents/{docId})
// ----------------------------------------------------
export async function deleteDocument(docId) {
    try {
        await $.ajax({
            url: `/api/finance/documents/${docId}`,
            method: 'DELETE'
        });
        console.log(`문서 ID ${docId} 삭제 성공`);
        return true;
    } catch (error) {
        console.error(`문서 ID ${docId} 삭제 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 6. 결재 상태 변경 (PUT, /api/finance/documents/{docId}/approval)
// ----------------------------------------------------
export async function changeApprovalStatus(docId, status) {
    try {
        const result = await $.ajax({
            url: `/api/finance/documents/${docId}/approval`,
            method: 'PUT',
            data: { status: status } // 쿼리 파라미터로 전송
        });
        console.log(`문서 ID ${docId} 상태 변경 성공:`, result);
        return result;
    } catch (error) {
        console.error(`문서 ID ${docId} 상태 변경 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 7. 예산 계정 조회 (GET, /api/finance/budget/accounts/{acctCode})
// ----------------------------------------------------
export async function getBudgetAccount(acctCode) {
    try {
        const data = await $.ajax({
            url: `/api/finance/budget/accounts/${acctCode}`,
            method: 'GET',
            dataType: 'json'
        });
        console.log(`예산 계정 ${acctCode} 조회:`, data);
        return data;
    } catch (error) {
        console.error(`예산 계정 ${acctCode} 조회 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 8. 예산 잔액 확인 (GET, /api/finance/budget/check)
// ----------------------------------------------------
export async function checkBudgetRemains(acctCode, amount) {
    try {
        const isAvailable = await $.ajax({
            url: '/api/finance/budget/check',
            method: 'GET',
            data: { acctCode: acctCode, amount: amount } // 쿼리 파라미터로 전송
        });
        console.log(`예산 ${acctCode} 잔액 확인 (금액 ${amount}):`, isAvailable);
        return isAvailable; // true 또는 false 반환
    } catch (error) {
        console.error('예산 잔액 확인 실패:', error);
        throw error;
    }
}

// ----------------------------------------------------
// 9. 예산 잔액 업데이트 (PUT, /api/finance/budget/{acctCode}/remains)
// ----------------------------------------------------
export async function updateBudgetRemains(acctCode, useAmount) {
    try {
        const result = await $.ajax({
            url: `/api/finance/budget/${acctCode}/remains`,
            method: 'PUT',
            data: { useAmount: useAmount } // 쿼리 파라미터 또는 form data로 전송
        });
        console.log(`예산 ${acctCode} 잔액 업데이트 성공:`, result);
        return result;
    } catch (error) {
        console.error(`예산 ${acctCode} 잔액 업데이트 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 10. 회계 전표 생성 (POST, /api/finance/documents/{docId}/aggregate)
// ----------------------------------------------------
export async function aggregateToSlip(docId) {
    try {
        const result = await $.ajax({
            url: `/api/finance/documents/${docId}/aggregate`,
            method: 'POST'
        });
        console.log(`문서 ID ${docId} -> 전표 생성 성공:`, result);
        return result;
    } catch (error) {
        console.error(`문서 ID ${docId} -> 전표 생성 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 11. 회계 전표 상세 조회 (GET, /api/finance/slips/{slipId})
// ----------------------------------------------------
export async function getSlip(slipId) {
    try {
        const data = await $.ajax({
            url: `/api/finance/slips/${slipId}`,
            method: 'GET',
            dataType: 'json'
        });
        console.log(`전표 ID ${slipId} 상세 조회:`, data);
        return data;
    } catch (error) {
        console.error(`전표 ID ${slipId} 상세 조회 실패:`, error);
        throw error;
    }
}

// ----------------------------------------------------
// 12. ERP 전송 상태 업데이트 (PUT, /api/finance/slips/{slipId}/transfer-status)
// ----------------------------------------------------
export async function updateTransferStatus(slipId, status) {
    try {
        const result = await $.ajax({
            url: `/api/finance/slips/${slipId}/transfer-status`,
            method: 'PUT',
            data: { status: status } // 쿼리 파라미터로 전송
        });
        console.log(`전표 ID ${slipId} 전송 상태 업데이트 성공:`, result);
        return result;
    } catch (error) {
        console.error(`전표 ID ${slipId} 전송 상태 업데이트 실패:`, error);
        throw error;
    }
}