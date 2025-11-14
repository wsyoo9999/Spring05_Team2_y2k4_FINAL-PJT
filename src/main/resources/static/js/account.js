export async function account_listAll(){


    let table = `<table>
                            <thead>
                            <tr>
                            <th>거래처 식별번호</th>
                            <th>거래처 이름</th>
                            <th>거래처 담당자</th>
                            <th>거래처 위치</th>
                            <th>거래처 연락처(전화)</th>
                            <th>거래처 연락처(이메일)</th>
                            <th>거래처 정보 수정</th>
                            </tr>
                            </thead>
                        `;
    let tbody;

    const data = await $.ajax({
        url: '/api/account/list',
        method: 'GET',
        dataType: 'json',

    });

    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){

        tbody += `<tr>
                        <td>
                          ${row.ac_id}
                        </td>
                        <td>
                          ${row.ac_name}
                        </td>
                        <td>
                            ${row.ac_manager}
                        </td>
                        <td>
                            ${row.ac_loc}
                        </td>
                        <td>
                            ${row.ac_phone}
                        </td>
                        <td>
                            ${row.ac_email}
                        </td> <td  class = "actions">
                            <button data-action = "edit" data-file="account" data-value="${row.ac_id}" data-fn="editAccount">
                                <i class="as fa-edit"></i></button>
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table>`;

    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="account" data-fn="addAccount">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export async function account_list(formData){

    const ac_id = formData.ac_id;
    const ac_name = formData.ac_name;
    const ac_loc = formData.ac_loc;

    let table = `<table>
                            <thead>
                            <tr>
                            <th>거래처 식별번호</th>
                            <th>거래처 이름</th>
                            <th>거래처 담당자</th>
                            <th>거래처 위치</th>
                            <th>거래처 연락처(전화)</th>
                            <th>거래처 연락처(이메일)</th>
                            <th>거래처 정보 수정</th>
                            </tr>
                            </thead>
                        `;
    let tbody;

    const data = await $.ajax({
        url: '/api/account/list',
        method: 'GET',
        data:{
            ac_id : ac_id,
            ac_name : ac_name,
            ac_loc : ac_loc
        },
        dataType: 'json',

    });

    tbody= `<tbody>`;
    console.log(data);
    $.each(data, function (i, row){

        tbody += `<tr>
                        <td>
                          ${row.ac_id}
                        </td>
                        <td>
                          ${row.ac_name}
                        </td>
                        <td>
                            ${row.ac_manager}
                        </td>
                        <td>
                            ${row.ac_loc}
                        </td>
                        <td>
                            ${row.ac_phone}
                        </td>
                        <td>
                            ${row.ac_email}
                        </td> <td  class = "actions">
                            <button data-action = "edit" data-file="account" data-value="${row.ac_id}" data-fn="editAccount">
                                <i class="as fa-edit"></i></button>
                        </td> 
                        </tr>`;
    })
    tbody += `</tbody></table>`;

    const actionRow = `
            <div class="table-actions-header">
                <button class="action-button btn-primary" data-action="add" data-file="account" data-fn="addAccount">
                    <i class="fas fa-plus-circle"></i> 신규 추가
                </button>
            </div>
        `;
    console.log(table)
    return actionRow + table + tbody;
}

export function  account_search_form(){
    return `
        <form data-file="transaction" data-fn="account_list">
            <div class="form-group">
                <label> 거래처 고유 번호:
                    <input type="number" name="ac_id" value="" />
                </label>
            </div>
            <div class="form-group">
                <label> 거래처 이름:
                    <input type="number" name="ac_name" value="" />
                </label>
            </div>
            <div class="form-group">
                <label> 거래처 위치:
                    <input type="date" name="ac_loc"  />
                </label>
            </div>
            <button type="button" data-action="search" class="search_btn" data-file="account" data-fn="account_list">
                    <i class="fas fa-search">검색</i>
            </button>                
                </form>`;

}

export function addAccount(){
    const url='./../popup/account/addAccount.html';
    const features = 'width=550,height=600,resizable=no,scrollbars=yes';
    window.open(url,'add_account',features).focus();
}

export async function editAccount(id) {
    const base = './../popup/account/editAccount.html';
    const url  = `${base}?ac_id=${encodeURIComponent(id)}`;
    const features = 'width=550,height=600,resizable=no,scrollbars=yes';
    const child = window.open(url, 'account_edit', features);
    if (child) child.focus();
}