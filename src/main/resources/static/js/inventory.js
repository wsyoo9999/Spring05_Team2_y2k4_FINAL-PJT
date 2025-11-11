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
                    <th>재고 번호</th>
                    <th>재고명</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>재고 상태</th>
                    <th>보관 위치</th>
                    <th>유통기한</th>
                    <th>구분</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `;

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
                <td>${Number(row.stock_qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${convertStatus(row.item_status)}</td>
                <td>${row.storage_location}</td>
                <td>${row.expiration_date}</td>
                <td>${convertGubun(row.gubun)}</td>
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
                    <th>재고 번호</th>
                    <th>재고명</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>재고 상태</th>
                    <th>보관 위치</th>
                    <th>유통기한</th>
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
                <td>${row.stock_id}</td>
                <td>${row.stock_name}</td>
                <td>$${Number(row.stock_qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${convertStatus(row.item_status)}</td>
                <td>${row.storage_location}</td>
                <td>${row.expiration_date}</td>
                <td>${convertGubun(row.gubun)}</td>
                <td class="actions">
                    <button id="stock_edit" data-value="${row.stock_id}">
                        <i class="fas fa-edit"></i>
                    </button>
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
        <label for="stock_name">물품명</label>
        <input type="text" id="stock_name" name="stock_name" placeholder="예: 드릴" />
      </div>

      <div class="form-group">
        <label for="stock_status">상태</label>
        <select id="stock_status" name="stock_status">
          <option value="">전체</option>
          <option value="0">정상</option>
          <option value="1">불량</option>
          <option value="2">폐기</option>
          <option value="3">반납</option>
          <option value="4">보류</option>
        </select>
      </div>

      <div class="form-group">
        <label for="storage_location">보관위치</label>
        <input type="text" id="storage_location" name="storage_location" placeholder="예: A-01-03" />
      </div>

      <!-- 버튼은 submit 유지, 기존 search_btn 클래스 보존 + 스타일 클래스 추가 -->
      <button type="submit" class="search_btn" data-actoin="search" data-file="inventory" data-fn="stock_list">
        <i class="fas fa-search" aria-hidden="true"></i><span>검색</span>
      </button>
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

// 물품 상태 코드 > 한글 변환
function convertGubun(code) {
    switch (code) {
        case 0: return "원자재";
        case 1: return "판매상품";
        default: return "-";
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
    const features = 'width=570,height=520,resizable=no,scrollbars=yes';
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
                    <th>순번</th>
                    <th>재고 코드</th>
                    <th>입고일</th>
                    <th>재고명</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>총 금액(원)</th>
                    <th>공급업체</th>
                    <th>담당자</th>
                    <th>비고</th>
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
        table += `
            <tr>
                <td> ${row.inbound_order}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${row.inbound_date}</td>
                <td>${row.stock_name}</td>
                <td>${Number(row.inbound_qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${Number(row.total_price).toLocaleString()}</td>
                <td>${row.supplier}</td>
                <td>${row.manager}</td>
                <td>${row.remark}</td>
                <td class="actions">
                    <button id="inbound_edit" data-value="${row.inbound_order}">
                        <i class="fas fa-edit"></i>
                     </button>
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
                    <th>순번</th>
                    <th>재고 코드</th>
                    <th>입고일</th>
                    <th>재고명</th>
                    <th>수량(개)</th>
                    <th>단가(원)</th>
                    <th>총 금액(원)</th>
                    <th>공급업체</th>
                    <th>담당자</th>
                    <th>비고</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td>${row.inbound_order}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${row.inbound_date}</td>
                <td>${row.stock_name}</td>
                <td>${Number(row.inbound_qty).toLocaleString()}</td>
                <td>${Number(row.unit_price).toLocaleString()}</td>
                <td>${Number(row.total_price).toLocaleString()}</td>
                <td>${row.supplier}</td>
                <td>${row.manager}</td>
                <td>${row.remark}</td>
                <td></td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return actionRow + table ;
}

export function inbound_search_form() {
    return `
        <form data-file="inbound" data-fn="inbound_list">
        <div class="form-group">
            <label>검색 카테고리</label>
            <select id="keywordType" name="keywordType">
                <option value="inbound_id">입고 번호</option>
                <option value="stock_id">재고 코드</option>
                <option value="item_name">물품명</option>
                <option value="supplier">공급업체</option>
            </select>
            
        </div>
        <div class="form-group">
            <input id="keyword" type="number" name="keyword" placeholder="예: 3001"/>
        </div>
        <div class="form-group">
            <label>입고일</label>
            <input type="date" name="inbound_date" />
        </div>
        <div class="form-group">
            <label>소비기한</label>
            <input type="date" name="expand_date" />
        </div>
            <button type="submit" class="search_btn" data-action="search" data-file="stock" data-fn="inbound_list">
                <i class="fas fa-search">검색</i>
            </button>
        </form>


        <script>
            const select = document.getElementById('keywordType');
            const input = document.getElementById('keyword');

            select.addEventListener('change', function() {
                const selected = this.value;

                if (selected === 'inbound_id' || selected === 'item_id') {
                    input.type = 'number';
                    input.placeholder = selected === 'inbound_id' ? '예: 3001' : '예: 1001';
                } else if (selected === 'item_name' || selected === 'supplier') {
                    input.type = 'text';
                    input.placeholder = selected === 'item_name' ? '예: 드릴' : '예: 삼성상사';
                }
            });
        </script>
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
    const url = `./../popup/inventory/viewInbound.html?inbound_order=${value}`;
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
                    <th>재고 코드</th>
                    <th>출고일</th>
                    <th>수량</th>
                    <th>출고처</th>
                    <th>담당자</th>
                    <th>비고</th>
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
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${row.outbound_date}</td>
                <td>${Number(row.outbound_qty).toLocaleString()}</td>
                <td>${row.outbound_location}</td>
                <td>${row.manager}</td>
                <td>${row.remark}</td>
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
                    <th>재고 코드</th>
                    <th>출고일</th>
                    <th>수량</th>
                    <th>출고처</th>
                    <th>담당자</th>
                    <th>비고</th>
                    <th>관리</th>                    
                </tr>
            </thead>
            <tbody>
    `;

    $.each(data.rows || data, function (i, row) {
        table += `
            <tr>
                <td> ${row.outbound_id}</td>
                <td id="stock_edit"
                    data-value="${row.stock_id}"
                    style="cursor:pointer"
                    onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                    onmouseout="this.style.color=''; this.style.fontWeight='';">
                        ${row.stock_id}
                </td>
                <td>${row.outbound_date}</td>
                <td>${Number(row.outbound_qty).toLocaleString()}</td>
                <td>${row.outbound_location}</td>
                <td>${row.manager}</td>
                <td>${row.remark}</td>
                <td></td>
            </tr>
        `;
    });

    table += `</tbody></table>`;
    return actionRow + table ;
}

export function outbound_search_form() {
    return `
        <form data-file="outbound" data-fn="outbound_list">
            <div class="form-group form-inline">
                <label for="keywordType">검색 카테고리</label>
                <select id="keywordType" name="keywordType">
                    <option value="outbound_id">출고 번호</option>
                    <option value="stock_id">재고 코드</option>
                    <option value="outbound_location">출고처</option>
                </select> 
               
            </div>
            <div class="form-group">
                <input id="keyword" type="number" name="keyword" placeholder="예: 2001"/>
            </div>
            <div class="form-group">
                <label>출고일</label>
                <input type="date" name="outbound_date" />
            </div>
            
            <div class="form-group">
                <label>출고일</label>
                <input type="date" name="outbound_date" />
            </div>

            <button type="submit" class="search_btn" data-action="search" data-file="stock" data-fn="outbond_list">
                <i class="fas fa-search">검색</i>
            </button>
<!--        </form>-->
        <script>
            const select = document.getElementById('keywordType');
            const input = document.getElementById('keyword');

            select.addEventListener('change', function() {
                const selected = this.value;

                // 숫자 검색 항목
                if (selected === 'outbound_id' || selected === 'stock_id') {
                    input.type = 'number';
                    input.placeholder = selected === 'outbound_id' ? '예: 2001' : '예: 1001';
                } 
                // 문자 검색 항목
                else if (selected === 'outbound_location') {
                    input.type = 'text';
                    input.placeholder = '예: 서울창고';
                }
            });
        </script>
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