export async function account_listAll(){


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
        if(row.status==1){
            edit = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 상태 변경 불가합니다')">
                        <i class="fas fa-gear"></i>
                        </button>`;
            editStatus = `<button type="button"
                                  onClick="alert('이미 도착완료인 주문은 수정 및 변경 불가합니다')">
                                <i class="fas fa-edit"></i>
                            </button>`
        }else{
            edit = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSaleStatus">
                                <i class="fas fa-gear"></i></button>`;
            editStatus = `<button data-action = "edit" data-file="transaction" data-value="${row.sale_id}" data-fn="editSale">
                                <i class="fas fa-edit"></i></button>`
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
                        <td>
                            ${row.status}
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
                            `+edit+`
                        </td> 
                         <td  class = "actions">
                            `+editStatus+`
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