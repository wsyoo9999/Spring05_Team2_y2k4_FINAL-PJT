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
}

