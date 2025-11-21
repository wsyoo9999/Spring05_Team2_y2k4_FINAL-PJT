export async function sale_listAll(){


    let table = `<table>
                            <thead>
                            <tr>
                            <th>담당자</th>
                            <th>거래처</th>
                            <th>주문일</th>
                            <th>요청 납기일</th>
                            <th>현 상태</th>
                            <th>상세 보기</th>
                            <th>상태 변경</th>
                            <th>판매 수정</th>
                            </tr>
                            </thead>
                        `;
    let tbody;

    const data = await $.ajax({
        url: '/api/transaction/sale/list',
        method: 'GET',
        dataType: 'json',

    });

    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        let edit;
        let editStatus;
        let statusColor;
        let statusText;
        if(row.status==1){  //현재 주문 배송중 상태
            edit = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSaleStatus">
                                <i class="fas fa-gear"></i></button>`;
            editStatus = `<button type="button"
                                  onClick="alert('이미 배송을 시작한 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `배송중`
            statusColor = '#f39c12'
        }else if(row.status==2){
            edit = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 상태 변경 불가합니다')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            editStatus = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `도착 완료`
            statusColor = '#27ae60'

        }
        else if(row.status==0){
            editStatus = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSaleStatus">
                                <i class="fas fa-gear"></i></button>`;
            edit = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSale">
                                <i class="fas fa-edit"></i></button>`
            statusText = `배송 준비중`
            statusColor = '#e74c3c'
        }else{
            edit = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            editStatus = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                                <i class="fas fa-edit"></i>
                            </button>`

            statusText = `결재 전`
            statusColor = '#e74c3c'
        }
        tbody += `<tr>
                        <td class="emp-id"
                            data-value="${row.emp_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';"
                            data-action="detail" data-file="transaction" data-fn="viewEmployee">
                          ${row.emp_name}
                        </td>
                        <td class="ac-id"
                            data-value="${row.ac_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';"
                            data-action="detail" data-file="transaction" data-fn="viewAccount">
                          ${row.ac_name}
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.due_date}
                        </td>
                        <td style="color:${statusColor}; font-weight:600;">
                        ${statusText}
                        </td>
                        <td class="ac-id"
                            data-value="${row.sale_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';" 
                            data-action="detail" data-file="transaction" data-fn="view_SaleDetails">
                          상세 보기
                        </td>
                         <td  class = "actions">
                            `+editStatus+`
                        </td> 
                         <td  class = "actions">
                            `+edit+`
                        </td> 
                        </tr>`;
        })
        tbody += `</tbody></table>`;

         const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="transaction" data-fn="addSale">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}



export async function sale_list(formData){
    const emp_id = formData.emp_id;     //담당자
    const ac_id = formData.ac_id;       //거래처
    const order_date = formData.order_date;     //주문일
    const due_date = formData.due_date;         //요청 납기일
    const status = formData.status;             //현상태

    let table = `<table>
                            <thead>
                            <tr>
                            <th>담당자</th>
                            <th>거래처</th>
                            <th>주문일</th>
                            <th>요청 납기일</th>
                            <th>현 상태</th>
                            <th>상세 보기</th>
                            <th>상태 변경</th>
                            <th>판매 수정</th>
                            </tr>
                            </thead>
                        `;

    let tbody;
    const data = await $.ajax({
        url: '/api/transaction/sale/list',
        method: 'GET',
        dataType: 'json',
        data:{
            emp_id : emp_id,
            ac_id : ac_id,
            order_date : order_date,
            due_date : due_date,
            status : status,
        }

    });
    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        let edit;
        let editStatus;
        let statusColor;
        let statusText;
        if(row.status==1){  //현재 주문 배송중 상태
            edit = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSaleStatus">
                                <i class="fas fa-gear"></i></button>`;
            editStatus = `<button type="button"
                                  onClick="alert('이미 배송을 시작한 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `배송중`
            statusColor = '#f39c12'
        }else if(row.status==2){
            edit = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 상태 변경 불가합니다')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            editStatus = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `도착 완료`
            statusColor = '#27ae60'

        }
        else if(row.status==0){
            editStatus = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSaleStatus">
                                <i class="fas fa-gear"></i></button>`;
            edit = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSale">
                                <i class="fas fa-edit"></i></button>`
            statusText = `배송 준비중`
            statusColor = '#e74c3c'
        }else{
            edit = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            editStatus = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                                <i class="fas fa-edit"></i>
                            </button>`

            statusText = `결재 전`
            statusColor = '#e74c3c'
        }
        tbody += `<tr>
                        <td class="emp-id"
                            data-value="${row.emp_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';"
                            data-action="detail" data-file="transaction" data-fn="viewEmployee">
                          ${row.emp_name}
                        </td>
                        <td class="ac-id"
                            data-value="${row.ac_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';"
                            data-action="detail" data-file="transaction" data-fn="viewAccount">
                          ${row.ac_name}
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.due_date}
                        </td>
                        <td style="color:${statusColor}; font-weight:600;">
                        ${statusText}
                        </td>
                        <td class="ac-id"
                            data-value="${row.sale_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';" 
                            data-action="detail" data-file="transaction" data-fn="view_SaleDetails">
                          상세 보기
                        </td>
                         <td  class = "actions">
                            `+editStatus+`
                        </td> 
                         <td  class = "actions">
                            `+edit+`
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table>`;


    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="transaction" data-fn="addSale">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export async function purchase_listAll(){

    let table = `<table>
                            <thead>
                            <tr>
                            <th>담당자</th>
                            <th>거래처</th>
                            <th>주문 수주일</th>
                            <th>납기일</th>
                            <th>현 상태</th>
                            <th>상세 보기</th>
                            <th>상태 변경</th>
                            <th>구매 수정</th>
                            </tr>
                            </thead>
                        `;

    let tbody;
    const data = await $.ajax({
        url: '/api/transaction/purchase/list',
        method: 'GET',
        dataType: 'json',


    });
    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        let edit;
        let editStatus;
        let statusText;
        let statusColor;
        if(row.status==1){
            editStatus = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 상태 변경 불가합니다')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            edit = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `도착 완료`
            statusColor = '#27ae60'
        }else if(row.status==0){
            editStatus = ` <button data-action = "edit" data-file="transaction" data-value="${row.purchase_id}" data-fn="editPurchaseStatus">
                                <i class="fas fa-gear"></i>
                            </button>`;
            edit= `<button data-action = "edit" data-file="transaction" data-value="${row.purchase_id}" data-fn="editPurchase">
                                <i class="fas fa-edit"></i>
                            </button>`

            statusText = `배송 준비`
            statusColor = '#f39c12'
        }else{
            editStatus = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            edit = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `결재 전`
            statusColor = '#e74c3c'
        }
        tbody += `<tr>
                        <td class="emp-id"
                            data-value="${row.emp_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';"
                            data-action="detail" data-file="transaction" data-fn="viewEmployee">
                          ${row.emp_name}
                        </td>
                        <td class="ac-id"
                            data-value="${row.ac_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';" 
                            data-action="detail" data-file="transaction" data-fn="viewAccount">
                          ${row.ac_name}
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.del_date}
                        </td>
                        <td style="color:${statusColor}; font-weight:600;">
                        ${statusText}
                        </td>
                        <td class="ac-id"
                            data-value="${row.purchase_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';" 
                            data-action="detail" data-file="transaction" data-fn="view_PurchaseDetails">
                          상세 보기
                        </td>
                         <td  class = "actions">
                            `+editStatus+`
                        </td> 
                         <td  class = "actions">
                            `+edit+`
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table>`;


    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="transaction" data-fn="addPurchase">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export async function purchase_list(formData){
    const emp_id = formData.emp_id;     //담당자
    const ac_id = formData.ac_id;       //거래처
    const order_date = formData.order_date;     //주문일
    const del_date = formData.del_date;         //요청 납기일
    const status = formData.status;             //현상태

    let table = `<table>
                            <thead>
                            <tr>
                            <th>담당자</th>
                            <th>거래처</th>
                            <th>주문 수주일</th>
                            <th>납기일</th>
                            <th>현 상태</th>
                            <th>상세 보기</th>
                            <th>상태 변경</th>
                            <th>구매 수정</th>
                            </tr>
                            </thead>
                        `;

    let tbody;
    const data = await $.ajax({
        url: '/api/transaction/purchase/list',
        method: 'GET',
        dataType: 'json',
        data:{
            emp_id : emp_id,
            ac_id : ac_id,
            order_date : order_date,
            del_date : del_date,
            status : status,
        }

    });
    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        let edit;
        let editStatus;
        let statusText;
        let statusColor;
        if(row.status==1){
            editStatus = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 상태 변경 불가합니다')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            edit = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `도착 완료`
            statusColor = '#27ae60'
        }else if(row.status==0){
            editStatus = ` <button data-action = "edit" data-file="transaction" data-value="${row.purchase_id}" data-fn="editPurchaseStatus">
                                <i class="fas fa-gear"></i>
                            </button>`;
            edit= `<button data-action = "edit" data-file="transaction" data-value="${row.purchase_id}" data-fn="editPurchase">
                                <i class="fas fa-edit"></i>
                            </button>`

            statusText = `배송 준비`
            statusColor = '#f39c12'
        }else{
            editStatus = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            edit = `<button type="button"
                                  onClick="alert('아직 결재 전')">
                                <i class="fas fa-edit"></i>
                            </button>`
            statusText = `결재 전`
            statusColor = '#e74c3c'
        }
        tbody += `<tr>
                        <td class="emp-id"
                            data-value="${row.emp_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';"
                            data-action="detail" data-file="transaction" data-fn="viewEmployee">
                          ${row.emp_name}
                        </td>
                        <td class="ac-id"
                            data-value="${row.ac_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';" 
                            data-action="detail" data-file="transaction" data-fn="viewAccount">
                          ${row.ac_name}
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.del_date}
                        </td>
                        <td style="color:${statusColor}; font-weight:600;">
                        ${statusText}
                        </td>
                        <td class="ac-id"
                            data-value="${row.purchase_id}"
                            style="cursor:pointer"
                            onmouseover="this.style.color='#4A96D9'; this.style.fontWeight='700';" 
                            onmouseout="this.style.color=''; this.style.fontWeight='';" 
                            data-action="detail" data-file="transaction" data-fn="view_PurchaseDetails">
                          상세 보기
                        </td>
                         <td  class = "actions">
                            `+editStatus+`
                        </td> 
                         <td  class = "actions">
                            `+edit+`
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table>`;


    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="transaction" data-fn="addPurchase">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export function  sale_search_form(){
    return `
        <form data-file="transaction" data-fn="sale_list">
            <div class="form-group">
                <label> 담당자:
                    <input type="number" name="emp_id" value="" />
                </label>
            </div>
            <div class="form-group">
                <label> 거래처:
                    <input type="number" name="ac_id" value="" />
                </label>
            </div>
            <div class="form-group">
                <label> 주문일:
                    <input type="date" name="order_date"  />
                </label>
            </div>
            <div class="form-group">
                <label> 요청 납기일:
                    <input type="date" name="due_date"  />
                </label>
            </div>
            <div class="form-group">
                <label> 상태:
                    <input type="number" name="status"  />
                </label>
            </div>
            <button type="button" data-action="search" class="search_btn" data-file="transaction" data-fn="sale_list">
                    <i class="fas fa-search">검색</i>
            </button>                
                </form>`;

}

export function  purchase_search_form(){
    return `
        <form data-file="transaction" data-fn="purchase_list">
            <div class="form-group">
                <label> 담당자:
                    <input type="text" name="emp_id" value="" />
                </label>
            </div>
            <div class="form-group">    
                <label> 거래처:
                    <input type="text" name="ac_id" value="" />
                </label>
            </div>
            <div class="form-group">
                <label> 주문 수주일:
                    <input type="date" name="order_date"  />
                </label>
            </div>
            <div class="form-group">
                <label> 납기일:
                    <input type="date" name="del_date"  />
                </label>
            </div>
            <div class="form-group">
                <label> 상태:
                    <input type="number" name="status"  />
                </label>
            </div>
            <button type="button" data-action="search" data-file="transaction" data-fn="purchase_list">
                <i class="fas fa-search">검색</i>
            </button>                                
                </label>
                </form>`;

}

export function addSale(){
    const url='./../popup/addSale.html';
    const features = 'width=570,height=700,resizable=no,scrollbars=yes';
    window.open(url,'add_Sale',features).focus();
}

export function addPurchase() {
    const url = './../popup/addPurchase.html';
    const features = 'width=570,height=700,resizable=no,scrollbars=yes';
    window.open(url, 'add_Sale', features).focus();
}
export function view_PurchaseDetails(id){
    const base = './../popup/transaction/viewPurchaseDetails.html';
    const url  = `${base}?purchase_id=${encodeURIComponent(id)}`;
    const features = 'width=570,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'purchase_details', features);
    if (child) child.focus();
}

export function view_SaleDetails(id){
    const base = './../popup/transaction/viewSaleDetails.html';
    const url  = `${base}?sale_id=${encodeURIComponent(id)}`;
    const features = 'width=570,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'sale_details', features);
    if (child) child.focus();

}

export async function editPurchase(id) {
    const base = './../popup/transaction/editPurchase.html';
    const url  = `${base}?purchase_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'purchase_edit', features);
    if (child) child.focus();
}

export async function editPurchaseStatus(id) {
    const base = './../popup/transaction/editPurchaseStatus.html';
    const url  = `${base}?purchase_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'ps_edit', features);
    if (child) child.focus();
}


export function editSale(id) {
    const base = './../popup/transaction/editSale.html';
    const url  = `${base}?sale_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'sale_edit', features);
    if (child) child.focus();
}

export async function editSaleStatus(id) {
    const base = './../popup/transaction/editSaleStatus.html';
    const url  = `${base}?sale_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'ss_edit', features);
    if (child) child.focus();
}

export async function  viewEmployee(id) {
    const base = './../popup/hr/viewEmployee.html';
    const url  = `${base}?emp_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'view_emp', features);
    if (child) child.focus();
}

export async function  viewAccount(id) {
    const base = './../popup/account/viewAccount.html';
    const url  = `${base}?ac_id=${encodeURIComponent(id)}`;
    const features = 'width=1000,height=700,resizable=no,scrollbars=yes';
    const child = window.open(url, 'view_ac', features);
    if (child) child.focus();
}