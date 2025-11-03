export function sale_listAll(){

}

export function sale_list(formData){
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
            del_date: del_date,
            status: status,
        }
    }).then(function(data){
        table+= `<tbody>`;

        $.each(data.rows, function (i, row){
            table += `<tr>
                        <td>${row.id}</td>`
        })
    })
    table += `</tbody></table>`;
    return table;
}

export function purchase_listAll(){

}

export function purchase_list(formData){

}