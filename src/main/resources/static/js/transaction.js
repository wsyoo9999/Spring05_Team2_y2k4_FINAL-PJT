export async function sale_listAll(){

    let table = `<table>
                            <thead>
                            <tr>
                            <th>담당자</th>
                            <th>거래처</th>
                            <th>주문일</th>
                            <th>요청 납기일</th>
                            <th>현 상태</th>
                            
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
        tbody += `<tr>
                        <td>${row.emp_id}</td>
                        <td>${row.client_id}</td>
                        <td>${row.order_date}</td>
                        <td>${row.due_date}</td>
                        <td>${row.status}</td>
                        </tr>`
        })
        tbody += `</tbody></table><input type="button" data-action="add" data-file="transaction" data-fn="addSale" value="추가">`;


    console.log(table)
    return table+tbody;
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
                            
                            </tr>
                            </thead>
                        `;

    const data = await $.ajax({
        url: '/api/transaction/sale/list',
        method: 'GET',
        dataType: 'json',

    });
    let tbody;
    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){
        tbody += `<tr>
                        <td>${row.emp_id}</td>
                        <td>${row.client_id}</td>
                        <td>${row.order_date}</td>
                        <td>${row.due_date}</td>
                        <td>${row.status}</td>
                        </tr>`
    })
    tbody += `</tbody></table>`;
    table += `</tbody></table>
    <input type="button" data-file="transaction" data-fn="addSale" data-action="add">`;
    return table;
}

export function purchase_listAll(){

}

export function purchase_list(formData){

}

export function search_form(){
    const search_bar = `<form data-file="transaction" data-fn="sale_list">
                                <input type="text" name="emp_id" value="" />
                                <input type="text" name="client_id" value="" />
                                <input type="hidden" name="_token" value="" />
                                <input type="date" name="order_date"  />
                                <input type="date" name="due_date"  />
                                <input type="button" data-file="transaction" data-fn="sale_list" value = "검색">
                                </form>`

    return search_bar;

}

export function addSale(){
    const url='./../popup/addSale.html';
    const features = 'width=570,height=350,resizable=no,scrollbars=yes';
    window.open(url,'add_Sale',features).focus();


}