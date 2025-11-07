export async function sale_listAll(){


    let table = `<table>
                            <thead>
                            <tr>
                            <th>담당자</th>
                            <th>거래처</th>
                            <th>주문일</th>
                            <th>요청 납기일</th>
                            <th>현 상태</th>
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
        tbody += tbody +=`<tr>
                        <td>
                            <input type="button" value="${row.emp_id}" data-action = "detail" data-file="transaction" data-fn="addSale">    <!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            <input type="button" value="${row.client_id}" data-action = "detail" data-file="transaction" data-fn="addSale"><!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.due_date}
                        </td>
                        <td>
                            ${row.status}
                        </td>
                        <td>
                            <input type="button" value="상태 변경" data-action = "edit" data-file="transaction" data-fn="editSaleStatus">
                        </td>  
                         <td>
                            <input type="button" value="수정" data-action = "edit" data-file="transaction" data-fn="editSale">
                        </td> 
                        </tr>`;
        })
        tbody += `</tbody></table>`;

         const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addSale">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}



export async function sale_list(formData){
    const emp_id = formData.emp_id;     //담당자
    const client_id = formData.client_id;       //거래처
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
            client_id : client_id,
            order_date : order_date,
            due_date : due_date,
            status : status,
        }

    });
    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        tbody +=`<tr>
                        <td>
                            <input type="button" value="${row.emp_id}" data-action = "detail" data-file="transaction" data-fn="addSale">    <!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            <input type="button" value="${row.client_id}" data-action = "detail" data-file="transaction" data-fn="addSale"><!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.due_date}
                        </td>
                        <td>
                            ${row.status}
                        </td>
                        <td>
                            <input type="button" value="상태 변경" data-action = "edit" data-file="transaction" data-fn="editSaleStatus">
                        </td>  
                         <td>
                            <input type="button" value="수정" data-action = "edit" data-file="transaction" data-fn="editSale">
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table><input type="button" data-action="add" data-file="transaction" data-fn="addSale" value="추가">`;


    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addSale">
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
        tbody += `<tr>
                        <td>
                            <input type="button" value="${row.emp_id}" data-action = "detail" data-file="transaction" data-fn="addPurchase">    <!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            <input type="button" value="${row.client_id}" data-action = "detail" data-file="transaction" data-fn="addPurchase"><!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.del_date}
                        </td>
                        <td>
                            ${row.status}
                        </td>
                        <td>
                            <input type="button" value="상태 변경" data-action = "edit" data-file="transaction" data-fn="editPurchaseStatus">
                        </td>  
                         <td>
                            <input type="button" value="수정" data-action = "edit" data-file="transaction" data-fn="editPurchase">
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table><input type="button" data-action="add" data-file="transaction" data-fn="addPurchase" value="추가">`;


    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addPurchase">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export async function purchase_list(formData){
    const emp_id = formData.emp_id;     //담당자
    const client_id = formData.client_id;       //거래처
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
            client_id : client_id,
            order_date : order_date,
            del_date : del_date,
            status : status,
        }

    });
    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        tbody += `<tr>
                        <td>
                            <input type="button" value="${row.emp_id}" data-action = "detail" data-file="transaction" data-fn="addPurchase">    <!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            <input type="button" value="${row.client_id}" data-action = "detail" data-file="transaction" data-fn="addPurchase"><!-- 추후 data-file과 data-fn의 수정 필요(다른 테이블 참조)-->
                        </td>
                        <td>
                            ${row.order_date}
                        </td>
                        <td>
                            ${row.del_date}
                        </td>
                        <td>
                            ${row.status}
                        </td>
                        <td>
                            <input type="button" value="상태 변경" data-action = "edit" data-file="transaction" data-fn="editPurchaseStatus">
                        </td>  
                         <td>
                            <input type="button" value="수정" data-action = "edit" data-file="transaction" data-fn="editPurchase">
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table><input type="button" data-action="add" data-file="transaction" data-fn="addPurchase" value="추가">`;


    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="inventory" data-fn="addPurchase">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export function  sale_search_form(){
    return `<form data-file="transaction" data-fn="sale_list">
                                <label> 담당자:
                                    <input type="text" name="emp_id" value="" />
                                </label>
                                <label> 거래처:
                                    <input type="text" name="client_id" value="" />
                                </label>
                                <label> 주문일:
                                    <input type="date" name="order_date"  />
                                </label>
                                <label> 요청 납기일:
                                    <input type="date" name="due_date"  />
                                </label>
                                    <label> 상태:
                                <input type="button" data-file="transaction" data-fn="sale_list" data-action = "search" value = "검색">
                                </label>
                                </form>`;

}

export function  purchase_search_form(){
    return `<form data-file="transaction" data-fn="purchase_list">
                                <label> 담당자:
                                    <input type="text" name="emp_id" value="" />
                                </label>
                                <label> 거래처:
                                    <input type="text" name="client_id" value="" />
                                </label>
                                <label> 주문 수주일:
                                    <input type="date" name="order_date"  />
                                </label>
                                <label> 납기일:
                                    <input type="date" name="del_date"  />
                                </label>
                                    <label> 상태:
                                <input type="button" data-file="transaction" data-fn="purchase_list" data-action = "search" value = "검색">
                                </label>
                                </form>`;

}

export function addSale(){
    const url='./../popup/addSale.html';
    const features = 'width=570,height=350,resizable=no,scrollbars=yes';
    window.open(url,'add_Sale',features).focus();


}

export function addPurchase() {
    const url = './../popup/addPurchase.html';
    const features = 'width=570,height=350,resizable=no,scrollbars=yes';
    window.open(url, 'add_Sale', features).focus();
}

export async function editPurchase() {
    return addPurchase();
}

export async function editPurchaseStatus() {
    return addPurchase();
}


export async function editSale() {
    return addSale();
}

export async function editSaleStatus() {
    return addSale();
}