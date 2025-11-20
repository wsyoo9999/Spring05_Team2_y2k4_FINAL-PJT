// =============================================물품=============================================


// 물품 목록 전체 조회
export async function stock_listAll() {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addStock">
                    <i class="fas fa-plus-circle"></i> 재고 추가
                </button>
            </div>
        `;
    let table = `

        <table border="1" style="width:100%; border-collapse:collapse;">
            <thead>
                <tr>
                    <th>재고 코드</th>
                    <th>재고명</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>보관 위치</th>
                    <th>요청 수량</th>
                    <th>판매자</th>
                    <th>구분</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `

    const data = await $.ajax({
        url: '/api/inventory/stock',
        method: 'GET',
        dataType: 'json'
    });

    $.each(data, function (i, row) {
        table += `
            <tr>
                <td> ${row.stock_id}</td>
                <td>${row.stock_name}</td>
                <td>${Number(row.qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${row.location}</td>
                <td>${Number(row.acquired_qty).toLocaleString()}</td>
                <td>${row.ac_name ?? '-'}</td>
                <td>${convertGubun(row.type)}</td>
                <td class="actions">                    
                    <button id="stock_edit" data-value="${row.stock_id}">
                        <i class="fas fa-edit"></i>
                    </button>
                </td>
            </tr>
        `;
    });


    table += `</tbody></table>`;

    return actionRow + table ;
}

// 물품 목록 조건 검색 조회
export async function stock_list(formData) {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addStock">
                    <i class="fas fa-plus-circle"></i> 재고 추가
                </button>
            </div>
        `;

    let table = `
        <table style="width:100%; border-collapse:collapse;">
            <thead>
                <tr>
                    <th>재고 코드</th>
                    <th>재고명</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>보관 위치</th>
                    <th>요청 수량</th>
                    <th>판매자</th>
                    <th>구분</th>
                    <th>관리</th>                        
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/inventory/stock',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    $.each(data, function (i, row) {
        table += `
            <tr>
                <td> ${row.stock_id}</td>
                <td>${row.stock_name}</td>
                <td>${Number(row.qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${row.location}</td>
                <td>${Number(row.acquired_qty).toLocaleString()}</td>
                <td>${row.ac_name ?? '-'}</td>
                <td>${convertGubun(row.type)}</td>
                <td class="actions">                    
                    <button id="stock_edit" data-value="${row.stock_id}">
                        <i class="fas fa-edit"></i>
                    </button>
                </td>
            </tr>
        `;
    });


    table += `</tbody></table>`;
    return actionRow + table;
}

export function stock_search_form() {
    return `
    <form data-file="inventory" data-fn="items_list">
      <div class="form-group">
        <label for="stock_name">재고명
        <input type="text" id="stock_name" name="stock_name" placeholder="검색" />
        </label>
      </div> 

      <div class="form-group">
        <label for="location">보관위치
        <input type="text" id="location" name="location" placeholder="검색" />
        </label>
      </div>
      
      <div class="form-group">
            <label>구분
            <select id="type" name="type">
                <option value="">전체</option>
                <option value="0">원자재</option>
                <option value="1">판매상품</option>
            </select>
            </label>
      </div>

      <button type="button" class="search_btn" data-action="search" data-file="inventory" data-fn="stock_list">
        <i class="fas fa-search" aria-hidden="true"></i><span>검색</span>
      </button>
    </form>
  `;
}

// 물품 상태 코드 > 한글 변환
function convertGubun(code) {
    switch (code) {
        case 0: return "원자재";
        case 1: return "판매상품";
        default: return "전체";
    }
}

// 입고 등록 팝업 호출
export function addStock(){
    const url='./../popup/inventory/addStock.html';
    const features = 'width=570,height=545,resizable=no,scrollbars=yes';
    window.open(url,'add_stock',features).focus();
}

// 편집 버튼 클릭 감지 후 editstock 호출
$(document).on('click', '#stock_edit', function() {
    const value = $(this).data('value');
    editStock(value);
});

// 재고 조회 팝업 호출
export function editStock(value){
    console.log('클릭된 값:', value);
    const url = `./../popup/inventory/viewStock.html?stock_id=${value}`;
    const features = 'width=570,height=400,resizable=no,scrollbars=yes';
    window.open(url, 'view_stock', features).focus();
}


// =============================================입고=============================================

// 입고 목록 전체 조회
export async function inbound_listAll() {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addInbound">
                    <i class="fas fa-plus-circle"></i> 입고 등록
                </button>
            </div>
        `;

    let table = `
        <table>
            <thead>
                <tr>
                    <th>입고번호</th>
                    <th>입고일</th>
                    <th>재고코드</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>총액</th>
                    <th>담당자</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/inventory/inbound',
        method: 'GET',
        dataType: 'json',
    });

    $.each(data.rows || data, function (i, row) {
        const total_price = row.total_price;
        table += `
            <tr>
                <td>${row.inbound_id}</td>
                <td>${row.inbound_date}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${Number(row.inbound_qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${Number(total_price).toLocaleString()}</td>
                <td>${row.emp_name}</td>  
                <td class="actions">
                    <button id="inbound_edit" data-value="${row.inbound_id}">
                        <i class="fas fa-edit"></i>
                     </button>
                </td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return actionRow + table ;
}

// 입고 목록 조건 검색
export async function inbound_list(formData) {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addStock">
                    <i class="fas fa-plus-circle"></i> 입고 등록
                </button>
            </div>
        `;

    const data = await $.ajax({
        url: '/api/inventory/inbound',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    let table = `
        <table>
            <thead>
                <tr>
                    <th>입고번호</th>
                    <th>입고일</th>
                    <th>재고코드</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>총액</th>
                    <th>담당자</th>
                    <th>관리</th>            
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        const total_price = row.total_price;
        table += `
            <tr>
                <td>${row.inbound_id}</td>
                <td>${row.inbound_date}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${Number(row.inbound_qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${Number(total_price).toLocaleString()}</td>
                <td>${row.emp_name}</td>  
                <td class="actions">
                    <button id="inbound_edit" data-value="${row.inbound_id}">
                        <i class="fas fa-edit"></i>
                     </button>
                </td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return actionRow + table ;
}

export function inbound_search_form() {
    return `
        <form data-file="inventory" data-fn="inbound_list">
        <div class="form-group">
          <label for="stock_id">재고 코드
          <input type="text" id="stock_id" name="stock_id" placeholder="검색" />
          </label>
        </div> 
        <div class="form-group">
            <label for="emp_name">담당자
            <input type="text" id="emp_name" name="emp_name" placeholder="검색" />
            </label>
        </div>
        <div class="form-group">
            <label>입고일</label>
            <input type="date" name="inbound_date" />
        </div>
        <div class="form-group">
            <label for="approval"> 승인상태</label>
            <select id="approval" name="approval">
                <option value="">전체</option>
                <option value="0">대기</option>
                <option value="1">승인</option>
                <option value="2">반려</option>
            </select>
        </div>
            <button type="button" class="search_btn" data-action="search" data-file="inventory" data-fn="inbound_list">
                <i class="fas fa-search">검색</i>
            </button>
        </form>
    `;
}


// 입고 등록 팝업 호출
export function addInbound(){
    const url='./../popup/inventory/addInbound.html';
    const features = 'width=570,height=620,resizable=no,scrollbars=yes';
    window.open(url,'add_inbound',features).focus();
}

// 편집 버튼 클릭 감지 후 viewStock 호출
$(document).on('click', '#inbound_edit', function() {
    const value = $(this).data('value');
    editInbound(value);
});

// 입고 조회 팝업 호출
export function editInbound(value){
    console.log('클릭된 값:', value);
    const url = `./../popup/inventory/viewInbound.html?inbound_id=${value}`;
    const features = 'width=570,height=620,resizable=no,scrollbars=yes';
    window.open(url, 'view_inbound', features).focus();
}

// =============================================출고=============================================

// 출고 목록 전체 조회
export async function outbound_listAll() {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addOutbound">
                    <i class="fas fa-plus-circle"></i> 출고 등록
                </button>
            </div>
        `;
    let table = `
        <table>
            <thead>
                <tr>
                    <th>출고 번호</th>
                    <th>출고일</th>
                    <th>재고코드</th>
                    <th>수량(개)</th>
                    <th>출고처</th>
                    <th>담당자</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `;

    const data = await $.ajax({
        url: '/api/inventory/outbound',
        method: 'GET',
        dataType: 'json',
    });

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.outbound_id}</td>
                <td>${row.outbound_date}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${row.outbound_qty}</td>
                <td>${row.ac_name}</td>  
                <td>${row.emp_name}</td>  
                <td class="actions">
                    <button id="outbound_edit" data-value="${row.outbound_id}">
                        <i class="fas fa-edit"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return actionRow + table ;
}

// 출고 목록 조건 검색
export async function outbound_list(formData) {
    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addOutbound">
                    <i class="fas fa-plus-circle"></i> 출고 등록
                </button>
            </div>
        `;

    const data = await $.ajax({
        url: '/api/inventory/outbound',
        method: 'GET',
        dataType: 'json',
        data: formData
    });

    let table = `
        <table>
            <thead>
                <tr>
                    <th>출고 번호</th>
                    <th>출고일</th>
                    <th>재고코드</th>
                    <th>수량(개)</th>
                    <th>출고처</th>
                    <th>담당자</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.outbound_id}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${row.outbound_qty}</td>
                <td>${row.ac_name}</td>  
                <td>${row.emp_name}</td>  
                <td>${row.outbound_date}</td>
                <td class="actions">
                    <button id="outbound_edit" data-value="${row.outbound_id}">
                        <i class="fas fa-edit"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return actionRow + table ;
}

export function outbound_search_form() {
    return `
        <form data-file="inventory" data-fn="outbound_list">
            <div class="form-group">
              <label for="stock_id">재고 코드
              <input type="text" id="stock_id" name="stock_id" placeholder="검색" />
              </label>
            </div> 
            <div class="form-group">
                <label for="ac_name">출고처
                <input type="text" id="ac_name" name="ac_name" placeholder="검색" />
                </label>
            </div>
            <div class="form-group">
                <label for="emp_name">담당자
                <input type="text" id="emp_name" name="emp_name" placeholder="검색" />
                </label>
            </div>
            <div class="form-group">
                <label>출고일</label>
                <input type="date" name="outbound_date" />
            </div>

            <button type="button" class="search_btn" data-action="search" data-file="inventory" data-fn="outbound_list">
                <i class="fas fa-search">검색</i>
            </button>
    `;
}

// 출고 등록 팝업 호출
export function addOutbound(){
    const url='./../popup/inventory/addOutbound.html';
    const features = 'width=570,height=540,resizable=no,scrollbars=yes';
    window.open(url,'add_outbound',features).focus();
}

// 편집 버튼 클릭 감지 후 viewStock 호출
$(document).on('click', '#outbound_edit', function() {
    const value = $(this).data('value');
    editOutbound(value);
});

// 재고 조회 팝업 호출
export function editOutbound(value){
    console.log('클릭된 값:', value);
    const url = `./../popup/inventory/viewOutbound.html?outbound_id=${value}`;
    const features = 'width=570,height=515,resizable=no,scrollbars=yes';
    window.open(url, 'view_outbound', features).focus();
}