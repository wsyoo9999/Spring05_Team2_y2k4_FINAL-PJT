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
        tbody += `</tbody></table>`;


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

    $.ajax({
        url: '/api/transaction/sale/list',
        method: 'get',
        dataType: 'json',
        data : {
            emp_id: emp_id,
            client_id: client_id,
            order_date: order_date,
            del_date: due_date,
            status: status,
        }
    }).then(function(data){
        table+= `<tbody>`;

        $.each(data.rows, function (i, row){
            table += `<tr>
                        <td>${row.emp_id}</td>
                        <td>${row.client_id}</td>
                        <td>${row.order_date}</td>
                        <td>${row.due_date}</td>
                        <td>${row.status}</td>
                        </tr>`
        })
    })
    table += `</tbody></table>`;
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
                                </form>`

    return search_bar;

}