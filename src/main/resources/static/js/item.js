// =============================물품=============================


// 물품 목록 전체 조회
export async function items_listAll() {
    let table = `
        <table border="1" style="width:100%; border-collapse:collapse;">
            <thead>
                <tr>
                    <th>물품 번호</th>
                    <th>물품명</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>총 금액</th>
                    <th>물품 상태</th>
                    <th>보관 위치</th>
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/stock/item',
        method: 'GET',
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        table += `
            <tr>
                <td>${row.item_id}</td>
                <td>${row.item_name}</td>
                <td>${row.item_qty}</td>
                <td>${Number(row.unit_price).toLocaleString()} 원</td>
                <td>${Number(row.total_price).toLocaleString()} 원</td>
                <td>${convertStatus(row.item_status)}</td>
                <td>${row.storage_location}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

// 물품 목록 조건 검색 조회
export async function items_list(formData) {
    let table = `
        <table border="1" style="width:100%; border-collapse:collapse;">
            <thead>
                <tr>
                    <th>물품 번호</th>
                    <th>물품명</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>총 금액</th>
                    <th>물품 상태</th>
                    <th>보관 위치</th>
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/stock/item',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    $.each(data, function (i, row) {
        table += `
            <tr>
                <td>${row.item_id}</td>
                <td>${row.item_name}</td>
                <td>${row.item_qty}</td>
                <td>${Number(row.unit_price).toLocaleString()} 원</td>
                <td>${Number(row.total_price).toLocaleString()} 원</td>
                <td>${convertStatus(row.item_status)}</td>
                <td>${row.storage_location}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

export function items_search_form() {
    return `
        <form data-file="item" data-fn="items_list">
            <label>물품명:</label>
            <input type="text" name="item_name" placeholder="예: 드릴" />

            <label>상태:</label>
            <select name="item_status">
                <option value="">전체</option>
                <option value="0">정상</option>
                <option value="1">불량</option>
                <option value="2">폐기</option>
                <option value="3">반납</option>
                <option value="4">보류</option>
            </select>

            <label>보관위치:</label>
            <input type="text" name="storage_location" placeholder="예: A-01-03" />

            <input type="button" class="search_btn"
                   data-file="item"
                   data-fn="items_list"
                   value="검색" />
        </form>
    `;
}


// 물품 상태 코드 > 한글 변환
function convertStatus(code) {
    switch (code) {
        case 0: return "정상";
        case 1: return "불량";
        case 2: return "폐기";
        case 3: return "반납";
        case 4: return "보류";
        default: return "-";
    }
}

// =============================입고=============================

// 입고 목록 전체 조회
export async function inbound_listAll() {
    let table = `
        <table>
            <thead>
                <tr>
                    <th>입고 번호</th>
                    <th>물품 번호</th>
                    <th>입고일</th>
                    <th>물품명</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>총 금액</th>
                    <th>공급업체</th>
                    <th>소비기한</th>
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/stock/inbound',
        method: 'GET',
        dataType: 'json',
    });

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.inbound_id}</td>
                <td>${row.item_id}</td>
                <td>${row.inbound_date}</td>
                <td>${row.item_name}</td>
                <td>${row.inbound_qty}</td>
                <td>${row.unit_price}</td>
                <td>${row.total_price}</td>
                <td>${row.supplier}</td>
                <td>${row.expand_date}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

// 입고 목록 조건 검색
export async function inbound_list(formData) {
    const data = await $.ajax({
        url: '/api/stock/inbound',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    let table = `
        <table>
            <thead>
                <tr>
                    <th>입고 번호</th>
                    <th>물품 번호</th>
                    <th>입고일</th>
                    <th>물품명</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>총 금액</th>
                    <th>공급업체</th>
                    <th>소비기한</th>
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.inbound_id}</td>
                <td>${row.item_id}</td>
                <td>${row.inbound_date}</td>
                <td>${row.item_name}</td>
                <td>${row.inbound_qty}</td>
                <td>${row.unit_price}</td>
                <td>${row.total_price}</td>
                <td>${row.supplier}</td>
                <td>${row.expand_date}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

export function inbound_search_form() {
    return `
        <form data-file="inbound" data-fn="inbound_list">
            <label>전체:</label>
            <select id="keywordType" name="keywordType">
                <option value="inbound_id">입고번호</option>
                <option value="item_id">물품번호</option>
                <option value="item_name">물품명</option>
                <option value="supplier">공급업체</option>
            </select>
            <input type="text" name="keyword" placeholder="예: " />

            <label>입고일:</label>
            <input type="date" name="inbound_date" />

            <label>소비기한:</label>
            <input type="date" name="expand_date" />

            <input type="button" class="search_btn"
                   data-file="inbound"
                   data-fn="inbound_list"
                   value="검색" />
        </form>
    `;
}


// =============================출고=============================

// 출고 목록 전체 조회
export async function outbound_listAll() {
    let table = `
        <table>
            <thead>
                <tr>
                    <th>출고 번호</th>
                    <th>물품 아이디</th>
                    <th>출고일</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>총 금액</th>
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/stock/outbound',
        method: 'GET',
        dataType: 'json',
    });

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.outbound_id}</td>
                <td>${row.item_id}</td>
                <td>${row.outbound_date}</td>
                <td>${row.outbound_qty}</td>
                <td>${row.unit_price}</td>
                <td>${row.total_price}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

// 출고 목록 조건 검색
export async function outbound_list(formData) {
    const data = await $.ajax({
        url: '/api/stock/outbound',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    let table = `
        <table>
            <thead>
                <tr>
                    <th>출고 번호</th>
                    <th>물품 아이디</th>
                    <th>출고일</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>총 금액</th>
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.outbound_id}</td>
                <td>${row.item_id}</td>
                <td>${row.outbound_date}</td>
                <td>${row.outbound_qty}</td>
                <td>${row.unit_price}</td>
                <td>${row.total_price}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}