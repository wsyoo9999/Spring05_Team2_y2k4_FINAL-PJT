// 물품 목록 전체 조회
export async function item_listAll() {
    let table = `
        <table>
            <thead>
                <tr>
                    <th>물품 아이디</th>
                    <th>물품명</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>물품 상태</th>
                    <th>보관 위치</th>
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/item',
        method: 'GET',
        dataType: 'json',
    });

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.item_id}</td>
                <td>${row.item_name}</td>
                <td>${row.item_qty}</td>
                <td>${row.unit_price}</td>
                <td>${row.item_status}</td>
                <td>${row.storage_location}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

// 물품 목록 조건 검색 조회
export async function item_list(formData) {
    const data = await $.ajax({
        url: '/api/item',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    let table = `
        <table>
            <thead>
                <tr>
                    <th>물품 아이디</th>
                    <th>물품명</th>
                    <th>수량</th>
                    <th>개당 가격</th>
                    <th>물품 상태</th>
                    <th>보관 위치</th>
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.item_id}</td>
                <td>${row.item_name}</td>
                <td>${row.item_qty}</td>
                <td>${row.unit_price}</td>
                <td>${row.item_status}</td>
                <td>${row.storage_location}</td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return table;
}

// 입고 목록 전체 조회
export async function inbound_listAll() {
    let table = `
        <table>
            <thead>
                <tr>
                    <th>입고 번호</th>
                    <th>물품 아이디</th>
                    <th>입고일</th>
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
        url: '/api/inbound',
        method: 'GET',
        dataType: 'json',
    });

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.inbound_id}</td>
                <td>${row.item_id}</td>
                <td>${row.inbound_date}</td>
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
        url: '/api/inbound',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    let table = `
        <table>
            <thead>
                <tr>
                    <th>입고 번호</th>
                    <th>물품 아이디</th>
                    <th>입고일</th>
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
        url: '/api/outbound',
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
        url: '/api/outbound',
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