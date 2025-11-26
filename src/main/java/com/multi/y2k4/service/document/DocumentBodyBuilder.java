package com.multi.y2k4.service.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import com.multi.y2k4.vo.production.WorkOrder;
import com.multi.y2k4.vo.production.Lot;
import com.multi.y2k4.vo.production.Defect;
import java.text.NumberFormat;

import java.util.*;

public class DocumentBodyBuilder {

    private final ObjectMapper objectMapper;

    public DocumentBodyBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    // 본문 내용(HTML)만 생성하는 메서드
    public String buildBody(String query_json) {
        if (query_json == null || query_json.isBlank()) {
            throw new IllegalArgumentException("query_json 이 비어 있습니다.");
        }
        StringBuilder sb = new StringBuilder();

        try {
            Map<String, Object> payload_map =
                    objectMapper.readValue(query_json, new TypeReference<Map<String, Object>>() {});

            int cat_id = (int) payload_map.get("cat_id");
            int tb_id = (int) payload_map.get("tb_id");
            int cd_id = (int) payload_map.get("cd_id");

            System.out.println("cat_id : "+cat_id);
            System.out.println("tb_id : "+tb_id);
            System.out.println("cd_id : "+cd_id);

            sb.append("<div class=\"doc-content\">");

            /*===============================재무관련=================================================*/
            if (cat_id == 0) {    //  재무 관련 문서

                if (tb_id == 0) {  //회사 수익 관리

                    if (cd_id == 0) {  //추가


                    } else if (cd_id == 1) {  //수정


                    } else if (cd_id == 2) {  //삭제



                    }

                } else if (tb_id == 1) {    //회사 지출 관리
                    if (cd_id == 0) {  //추가


                    } else if (cd_id == 1) {  //수정


                    } else if (cd_id == 2) {  //삭제



                    }
                }

                /*===============================판매 구매 관련=================================================*/

            } else if (cat_id == 1) { //판매 및 구매 관련


                /*--------------------------- 판매 ---------------------------*/
                if (tb_id == 0) {  //판매

                    /*======== 판매 추가 / 삭제 (cd_id = 0, 2) ========*/
                    if (cd_id == 0 || cd_id == 2) {

                        Object sale_obj    = payload_map.get("sale");
                        Object details_obj = payload_map.get("details");

                        Sale sale = (sale_obj != null)
                                ? objectMapper.convertValue(sale_obj, Sale.class)
                                : null;

                        List<SaleDetails> details_list = (details_obj != null)
                                ? objectMapper.convertValue(details_obj, new TypeReference<List<SaleDetails>>() {})
                                : Collections.emptyList();

                        // 제목
                        sb.append("<h3 class=\"doc-section-title\">")
                                .append(cd_id == 0 ? "판매 추가 요청 내용" : "판매 삭제 대상")
                                .append("</h3>");

                        // 판매 기본 정보
                        if (sale != null) {
                            sb.append("<h4 class=\"doc-subtitle\">판매 기본 정보</h4>");
                            sb.append("<table id=\"sale_basic_table\" class=\"doc-map-table\"><tbody>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">담당자 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(sale.getEmp_id())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">거래처 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(sale.getAc_id())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">주문일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(sale.getOrder_date())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">납기일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(sale.getDue_date())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">총 금액</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(sale.getTotal_price())))
                                    .append("</td></tr>");

                            sb.append("</tbody></table>");
                        }

                        // 판매 상세 항목
                        sb.append("<h4 class=\"doc-subtitle\">판매 상세 항목</h4>");

                        if (details_list == null || details_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table id=\"sale_details_table\" class=\"doc-list-table\">")
                                    .append("<thead><tr>")
                                    .append("<th>#</th>")
                                    .append("<th>재고 ID</th>")
                                    .append("<th>수량</th>")
                                    .append("<th>단가</th>")
                                    .append("<th>금액</th>")
                                    .append("</tr></thead><tbody>");

                            int row_index = 1;
                            for (SaleDetails d : details_list) {
                                int qty = d.getQty();
                                double unit_price = d.getPrice_per_unit();
                                double line_total = d.getTotal_price();

                                sb.append("<tr>")
                                        .append("<td>").append(row_index++).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getStock_id()))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(unit_price))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(line_total))).append("</td>")
                                        .append("</tr>");
                            }

                            sb.append("</tbody></table>");
                        }

                        /*======== 판매 수정 (cd_id = 1) ========*/
                    } else if (cd_id == 1) {

                        Object before_sale_obj    = payload_map.get("before_sale");
                        Object before_details_obj = payload_map.get("before_details");
                        Object after_sale_obj     = payload_map.get("after_sale");
                        Object after_details_obj  = payload_map.get("after_details");

                        Sale before_sale = (before_sale_obj != null)
                                ? objectMapper.convertValue(before_sale_obj, Sale.class)
                                : null;

                        Sale after_sale = (after_sale_obj != null)
                                ? objectMapper.convertValue(after_sale_obj, Sale.class)
                                : null;

                        List<SaleDetails> before_list = (before_details_obj != null)
                                ? objectMapper.convertValue(before_details_obj, new TypeReference<List<SaleDetails>>() {})
                                : Collections.emptyList();

                        List<SaleDetails> after_list = (after_details_obj != null)
                                ? objectMapper.convertValue(after_details_obj, new TypeReference<List<SaleDetails>>() {})
                                : Collections.emptyList();

                        sb.append("<h3 class=\"doc-section-title\">판매 수정 요청 내역</h3>");

                        // (선택) 판매 기본 정보 변경 전/후
                        if (before_sale != null || after_sale != null) {
                            sb.append("<h4 class=\"doc-subtitle\">판매 기본 정보 변경</h4>");
                            sb.append("<table id=\"sale_basic_diff_table\" class=\"doc-map-table\"><thead><tr>")
                                    .append("<th>항목</th>")
                                    .append("<th>변경 전</th>")
                                    .append("<th>변경 후</th>")
                                    .append("</tr></thead><tbody>");

                            // 담당자
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">담당자 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_sale == null ? "" : escape_html(String.valueOf(before_sale.getEmp_id())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_sale == null ? "" : escape_html(String.valueOf(after_sale.getEmp_id())))
                                    .append("</td></tr>");

                            // 거래처
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">거래처 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_sale == null ? "" : escape_html(String.valueOf(before_sale.getAc_id())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_sale == null ? "" : escape_html(String.valueOf(after_sale.getAc_id())))
                                    .append("</td></tr>");

                            // 주문일
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">주문일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_sale == null ? "" : escape_html(String.valueOf(before_sale.getOrder_date())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_sale == null ? "" : escape_html(String.valueOf(after_sale.getOrder_date())))
                                    .append("</td></tr>");

                            // 납기일
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">납기일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_sale == null ? "" : escape_html(String.valueOf(before_sale.getDue_date())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_sale == null ? "" : escape_html(String.valueOf(after_sale.getDue_date())))
                                    .append("</td></tr>");

                            // 총 금액
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">총 금액</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_sale == null ? "" : escape_html(String.valueOf(before_sale.getTotal_price())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_sale == null ? "" : escape_html(String.valueOf(after_sale.getTotal_price())))
                                    .append("</td></tr>");

                            sb.append("</tbody></table>");
                        }

                        // 변경 전 상세
                        sb.append("<h4 class=\"doc-subtitle\">변경 전 상세 항목</h4>");
                        if (before_list == null || before_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">이전 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table id=\"sale_before_details_table\" class=\"doc-list-table\">")
                                    .append("<thead><tr>")
                                    .append("<th>#</th>")
                                    .append("<th>재고 ID</th>")
                                    .append("<th>수량</th>")
                                    .append("<th>단가</th>")
                                    .append("<th>금액</th>")
                                    .append("</tr></thead><tbody>");

                            int row_index = 1;
                            for (SaleDetails d : before_list) {
                                int qty = d.getQty();
                                double unit_price = d.getPrice_per_unit();
                                double line_total = d.getTotal_price();

                                sb.append("<tr>")
                                        .append("<td>").append(row_index++).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getStock_id()))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(unit_price))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(line_total))).append("</td>")
                                        .append("</tr>");
                            }

                            sb.append("</tbody></table>");
                        }

                        // 변경 후 상세
                        sb.append("<h4 class=\"doc-subtitle\">변경 후 상세 항목</h4>");
                        if (after_list == null || after_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">변경 후 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table id=\"sale_after_details_table\" class=\"doc-list-table\">")
                                    .append("<thead><tr>")
                                    .append("<th>#</th>")
                                    .append("<th>재고 ID</th>")
                                    .append("<th>수량</th>")
                                    .append("<th>단가</th>")
                                    .append("<th>금액</th>")
                                    .append("</tr></thead><tbody>");

                            int row_index = 1;
                            for (SaleDetails d : after_list) {
                                int qty = d.getQty();
                                double unit_price = d.getPrice_per_unit();
                                double line_total = d.getTotal_price();

                                sb.append("<tr>")
                                        .append("<td>").append(row_index++).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getStock_id()))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(unit_price))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(line_total))).append("</td>")
                                        .append("</tr>");
                            }

                            sb.append("</tbody></table>");
                        }
                    }

                    /*--------------------------- 구매 ---------------------------*/
                } else if (tb_id == 1) {    //구매

                    /*======== 구매 추가 / 삭제 (cd_id = 0, 2) ========*/
                    if (cd_id == 0 || cd_id == 2) {

                        Object purchase_obj = payload_map.get("purchase");
                        Object details_obj  = payload_map.get("details");

                        Purchase purchase = (purchase_obj != null)
                                ? objectMapper.convertValue(purchase_obj, Purchase.class)
                                : null;

                        List<PurchaseDetails> details_list = (details_obj != null)
                                ? objectMapper.convertValue(details_obj, new TypeReference<List<PurchaseDetails>>() {})
                                : Collections.emptyList();

                        sb.append("<h3 class=\"doc-section-title\">")
                                .append(cd_id == 0 ? "구매 추가 요청 내용" : "구매 삭제 대상")
                                .append("</h3>");

                        // 구매 기본 정보
                        if (purchase != null) {
                            sb.append("<h4 class=\"doc-subtitle\">구매 기본 정보</h4>");
                            sb.append("<table id=\"purchase_basic_table\" class=\"doc-map-table\"><tbody>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">담당자 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(purchase.getEmp_id())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">거래처 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(purchase.getAc_id())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">주문일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(purchase.getOrder_date())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">예상 입고일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(purchase.getDel_date())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">총 금액</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(purchase.getTotal_price())))
                                    .append("</td></tr>");

                            sb.append("</tbody></table>");
                        }

                        // 구매 상세 정보
                        sb.append("<h4 class=\"doc-subtitle\">구매 상세 항목</h4>");
                        if (details_list == null || details_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table id=\"purchase_details_table\" class=\"doc-list-table\">")
                                    .append("<thead><tr>")
                                    .append("<th>#</th>")
                                    .append("<th>재고 ID</th>")
                                    .append("<th>주문 수량</th>")
                                    .append("<th>입고 수량</th>")
                                    .append("<th>단가</th>")
                                    .append("<th>금액</th>")
                                    .append("</tr></thead><tbody>");

                            int row_index = 1;
                            for (PurchaseDetails d : details_list) {
                                int purchase_qty = d.getPurchase_qty();
                                int qty          = d.getQty();
                                double unit_price = d.getPrice_per_unit();
                                double line_total = d.getTotal_price();

                                sb.append("<tr>")
                                        .append("<td>").append(row_index++).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getStock_id()))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(purchase_qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(unit_price))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(line_total))).append("</td>")
                                        .append("</tr>");
                            }

                            sb.append("</tbody></table>");
                        }

                        /*======== 구매 수정 (cd_id = 1) ========*/
                    } else if (cd_id == 1) {

                        Object before_purchase_obj    = payload_map.get("before_purchase");
                        Object before_details_obj     = payload_map.get("before_details");
                        Object after_purchase_obj     = payload_map.get("after_purchase");
                        Object after_details_obj      = payload_map.get("after_details");

                        Purchase before_purchase = (before_purchase_obj != null)
                                ? objectMapper.convertValue(before_purchase_obj, Purchase.class)
                                : null;

                        Purchase after_purchase = (after_purchase_obj != null)
                                ? objectMapper.convertValue(after_purchase_obj, Purchase.class)
                                : null;

                        List<PurchaseDetails> before_list = (before_details_obj != null)
                                ? objectMapper.convertValue(before_details_obj, new TypeReference<List<PurchaseDetails>>() {})
                                : Collections.emptyList();

                        List<PurchaseDetails> after_list = (after_details_obj != null)
                                ? objectMapper.convertValue(after_details_obj, new TypeReference<List<PurchaseDetails>>() {})
                                : Collections.emptyList();

                        sb.append("<h3 class=\"doc-section-title\">구매 수정 요청 내역</h3>");

                        // 기본 정보 변경 전/후
                        if (before_purchase != null || after_purchase != null) {
                            sb.append("<h4 class=\"doc-subtitle\">구매 기본 정보 변경</h4>");
                            sb.append("<table id=\"purchase_basic_diff_table\" class=\"doc-map-table\"><thead><tr>")
                                    .append("<th>항목</th>")
                                    .append("<th>변경 전</th>")
                                    .append("<th>변경 후</th>")
                                    .append("</tr></thead><tbody>");

                            // 담당자
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">담당자 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_purchase == null ? "" : escape_html(String.valueOf(before_purchase.getEmp_id())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_purchase == null ? "" : escape_html(String.valueOf(after_purchase.getEmp_id())))
                                    .append("</td></tr>");

                            // 거래처
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">거래처 ID</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_purchase == null ? "" : escape_html(String.valueOf(before_purchase.getAc_id())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_purchase == null ? "" : escape_html(String.valueOf(after_purchase.getAc_id())))
                                    .append("</td></tr>");

                            // 주문일
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">주문일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_purchase == null ? "" : escape_html(String.valueOf(before_purchase.getOrder_date())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_purchase == null ? "" : escape_html(String.valueOf(after_purchase.getOrder_date())))
                                    .append("</td></tr>");

                            // 입고일
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">예상 입고일</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_purchase == null ? "" : escape_html(String.valueOf(before_purchase.getDel_date())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_purchase == null ? "" : escape_html(String.valueOf(after_purchase.getDel_date())))
                                    .append("</td></tr>");

                            // 총 금액
                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">총 금액</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(before_purchase == null ? "" : escape_html(String.valueOf(before_purchase.getTotal_price())))
                                    .append("</td>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(after_purchase == null ? "" : escape_html(String.valueOf(after_purchase.getTotal_price())))
                                    .append("</td></tr>");

                            sb.append("</tbody></table>");
                        }

                        // 변경 전 상세
                        sb.append("<h4 class=\"doc-subtitle\">변경 전 상세 항목</h4>");
                        if (before_list == null || before_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">이전 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table id=\"purchase_before_details_table\" class=\"doc-list-table\">")
                                    .append("<thead><tr>")
                                    .append("<th>#</th>")
                                    .append("<th>재고 ID</th>")
                                    .append("<th>주문 수량</th>")
                                    .append("<th>입고 수량</th>")
                                    .append("<th>단가</th>")
                                    .append("<th>금액</th>")
                                    .append("</tr></thead><tbody>");

                            int row_index = 1;
                            for (PurchaseDetails d : before_list) {
                                int purchase_qty = d.getPurchase_qty();
                                int qty          = d.getQty();
                                double unit_price = d.getPrice_per_unit();
                                double line_total = d.getTotal_price();

                                sb.append("<tr>")
                                        .append("<td>").append(row_index++).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getStock_id()))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(purchase_qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(unit_price))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(line_total))).append("</td>")
                                        .append("</tr>");
                            }

                            sb.append("</tbody></table>");
                        }

                        // 변경 후 상세
                        sb.append("<h4 class=\"doc-subtitle\">변경 후 상세 항목</h4>");
                        if (after_list == null || after_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">변경 후 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table id=\"purchase_after_details_table\" class=\"doc-list-table\">")
                                    .append("<thead><tr>")
                                    .append("<th>#</th>")
                                    .append("<th>재고 ID</th>")
                                    .append("<th>주문 수량</th>")
                                    .append("<th>입고 수량</th>")
                                    .append("<th>단가</th>")
                                    .append("<th>금액</th>")
                                    .append("</tr></thead><tbody>");

                            int row_index = 1;
                            for (PurchaseDetails d : after_list) {
                                int purchase_qty = d.getPurchase_qty();
                                int qty          = d.getQty();
                                double unit_price = d.getPrice_per_unit();
                                double line_total = d.getTotal_price();

                                sb.append("<tr>")
                                        .append("<td>").append(row_index++).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getStock_id()))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(purchase_qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(qty))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(unit_price))).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(line_total))).append("</td>")
                                        .append("</tr>");
                            }

                            sb.append("</tbody></table>");
                        }
                    }
                }

                /*===============================생산 제조 관련=================================================*/

                /*===============================생산 제조 관련=================================================*/
            } else if (cat_id == 2) { // 생산/제조
                if (tb_id == 0) {  // 작업 지시서

                    // 1. 추가(0) 또는 삭제(2) 요청
                    if (cd_id == 0 || cd_id == 2) {
                        Object wo_obj = payload_map.get("workOrder");

                        WorkOrder wo = (wo_obj != null)
                                ? objectMapper.convertValue(wo_obj, WorkOrder.class)
                                : null;

                        sb.append("<h3 class=\"doc-section-title\">")
                                .append(cd_id == 0 ? "작업 지시서 등록 요청" : "작업 지시서 삭제 대상")
                                .append("</h3>");

                        if (wo != null) {
                            sb.append("<h4 class=\"doc-subtitle\">작업 지시 상세 정보</h4>");
                            sb.append("<table class=\"doc-map-table\"><tbody>");

                            if (wo.getWork_order_id() != null) {
                                sb.append("<tr>")
                                        .append("<th class=\"doc-data-label\">작업지시번호</th>")
                                        .append("<td class=\"doc-data-value\">")
                                        .append(escape_html(String.valueOf(wo.getWork_order_id())))
                                        .append("</td></tr>");
                            }

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">제품명 (코드)</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(wo.getStock_name() != null ? wo.getStock_name() : "-"))
                                    .append(" <span style='color:#888;'>(")
                                    .append(escape_html(String.valueOf(wo.getStock_id())))
                                    .append(")</span>")
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">담당자 (사번)</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(wo.getEmp_name() != null ? wo.getEmp_name() : "-"))
                                    .append(" <span style='color:#888;'>(")
                                    .append(escape_html(String.valueOf(wo.getEmp_id())))
                                    .append(")</span>")
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">작업 기간</th>")
                                    .append("<td class=\"doc-data-value\">")
                                    .append(escape_html(String.valueOf(wo.getStart_date())))
                                    .append(" ~ ")
                                    .append(escape_html(String.valueOf(wo.getDue_date())))
                                    .append("</td></tr>");

                            sb.append("<tr>")
                                    .append("<th class=\"doc-data-label\">목표 수량</th>")
                                    .append("<td class=\"doc-data-value\" style=\"font-weight:bold; color:#007bff;\">")
                                    .append(escape_html(String.valueOf(wo.getTarget_qty())))
                                    .append(" 개")
                                    .append("</td></tr>");

                            sb.append("</tbody></table>");
                        } else {
                            sb.append("<p class=\"doc-info\">작업 지시 정보가 없습니다.</p>");
                        }

                        // 2. 수정(1) 요청
                    } else if (cd_id == 1 && payload_map.containsKey("newStatus") && (int)payload_map.get("newStatus") == 3) {

                        Object wo_obj = payload_map.get("workOrder");
                        Object lots_obj = payload_map.get("lots");
                        Object defects_obj = payload_map.get("defects");

                        WorkOrder wo = (wo_obj != null) ? objectMapper.convertValue(wo_obj, WorkOrder.class) : null;
                        List<Lot> lots = (lots_obj != null) ? objectMapper.convertValue(lots_obj, new TypeReference<List<Lot>>() {
                        }) : Collections.emptyList();
                        List<Defect> defects = (defects_obj != null) ? objectMapper.convertValue(defects_obj, new TypeReference<List<Defect>>() {
                        }) : Collections.emptyList();

                        sb.append("<h3 class=\"doc-section-title\" style=\"color:#e74c3c;\">작업 지시서 폐기 요청서</h3>");

                        // 1. 기본 정보 & 진행률
                        if (wo != null) {
                            int target = wo.getTarget_qty() != null ? wo.getTarget_qty() : 0;
                            int good = wo.getGood_qty() != null ? wo.getGood_qty() : 0;
                            int defect = wo.getDefect_qty() != null ? wo.getDefect_qty() : 0;
                            double progress = target > 0 ? (double) good / target * 100 : 0;

                            sb.append("<h4 class=\"doc-subtitle\">진행 현황</h4>");
                            sb.append("<table class=\"doc-map-table\"><tbody>");

                            sb.append("<tr><th class=\"doc-data-label\">작업지시번호</th><td class=\"doc-data-value\">").append(wo.getWork_order_id()).append("</td></tr>");
                            sb.append("<tr><th class=\"doc-data-label\">제품명</th><td class=\"doc-data-value\">").append(escape_html(wo.getStock_name())).append("</td></tr>");
                            sb.append("<tr><th class=\"doc-data-label\">담당자</th><td class=\"doc-data-value\">").append(escape_html(wo.getEmp_name())).append("</td></tr>");

                            sb.append("<tr><th class=\"doc-data-label\">생산 수량</th><td class=\"doc-data-value\">")
                                    .append("목표: <b>").append(target).append("</b> / ")
                                    .append("양품: <b style='color:#27ae60'>").append(good).append("</b> / ")
                                    .append("불량: <b style='color:#e74c3c'>").append(defect).append("</b>")
                                    .append("</td></tr>");

                            sb.append("<tr><th class=\"doc-data-label\">진행률</th><td class=\"doc-data-value\">")
                                    .append(String.format("%.1f%%", progress))
                                    .append("</td></tr>");

                            sb.append("</tbody></table>");
                        }

                        // 2. Lot 내역
                        sb.append("<h4 class=\"doc-subtitle\">생산 실적 (Lot) 내역</h4>");
                        if (!lots.isEmpty()) {
                            sb.append("<table class=\"doc-list-table\"><thead><tr>")
                                    .append("<th>Lot ID</th><th>생산일</th><th>수량</th>")
                                    .append("</tr></thead><tbody>");
                            for (Lot lot : lots) {
                                sb.append("<tr>")
                                        .append("<td>").append(lot.getLot_id()).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(lot.getLot_date()))).append("</td>")
                                        .append("<td>").append(lot.getLot_qty()).append("</td>")
                                        .append("</tr>");
                            }
                            sb.append("</tbody></table>");
                        } else {
                            sb.append("<p class=\"doc-info\">등록된 Lot 실적이 없습니다.</p>");
                        }

                        // 3. 불량 내역
                        sb.append("<h4 class=\"doc-subtitle\" style=\"color:#e74c3c;\">불량 상세 내역</h4>");
                        if (!defects.isEmpty()) {
                            sb.append("<table class=\"doc-list-table\"><thead><tr>")
                                    .append("<th>불량코드</th><th>관련 Lot ID</th><th>불량 수량</th><th>발생일</th>")
                                    .append("</tr></thead><tbody>");
                            for (Defect d : defects) {
                                sb.append("<tr>")
                                        .append("<td>").append(d.getDefect_code()).append("</td>")
                                        .append("<td>").append(d.getLot_id()).append("</td>")
                                        .append("<td style='color:#e74c3c; font-weight:bold;'>").append(d.getDefect_qty()).append("</td>")
                                        .append("<td>").append(escape_html(String.valueOf(d.getDefect_date()))).append("</td>")
                                        .append("</tr>");
                            }
                            sb.append("</tbody></table>");
                        } else {
                            sb.append("<p class=\"doc-info\">등록된 불량 내역이 없습니다.</p>");
                        }
                    }
                }

                /*===============================재고 관련=================================================*/

            } else if (cat_id == 3) { //재고
                if (tb_id == 0) {  //추후 확장을 위함
                    if (cd_id == 0) {  //추가

                    } else if (cd_id == 1) {  //수정


                    } else if (cd_id == 2) {  //삭제


                    }
                }

                /*===============================인사 관련=================================================*/

            }else if (cat_id == 4) {

                // [1] 휴가 신청 (tb_id = 0)
                if (tb_id == 0) {
                    if (cd_id == 0) {
                        sb.append("<h3 class=\"doc-section-title\">휴가 신청서</h3>");
                        sb.append("<table class=\"doc-map-table\"><tbody>");

                        sb.append("<tr>")
                                .append("<th class=\"doc-data-label\">기안자</th>")
                                .append("<td class=\"doc-data-value\">")
                                .append(escape_html((String) payload_map.get("requesterName")))
                                .append("</td></tr>");

                        sb.append("<tr>")
                                .append("<th class=\"doc-data-label\">휴가 기간</th>")
                                .append("<td class=\"doc-data-value\">")
                                .append(escape_html((String) payload_map.get("startDate")))
                                .append(" ~ ")
                                .append(escape_html((String) payload_map.get("endDate")))
                                .append("</td></tr>");

                        sb.append("<tr>")
                                .append("<th class=\"doc-data-label\">신청 사유</th>")
                                .append("<td class=\"doc-data-value\">")
                                .append(escape_html((String) payload_map.get("reason")))
                                .append("</td></tr>");

                        sb.append("</tbody></table>");
                    }
                }

                // [2] 인사 발령 / 상태 변경
                else if (tb_id == 1) {
                    if (cd_id == 1) { // 수정(인사발령 요청)
                        sb.append("<h3 class=\"doc-section-title\">인사 발령(퇴직) 요청서</h3>");
                        sb.append("<table class=\"doc-map-table\"><tbody>");

                        sb.append("<tr>")
                                .append("<th class=\"doc-data-label\">대상자</th>")
                                .append("<td class=\"doc-data-value\">")
                                .append(escape_html((String) payload_map.get("targetEmpName")))
                                .append(" (사번: ").append(payload_map.get("targetEmpId")).append(")")
                                .append("</td></tr>");

                        sb.append("<tr>")
                                .append("<th class=\"doc-data-label\">변경 구분</th>")
                                .append("<td class=\"doc-data-value\">")
                                .append("재직 상태 변경 ( → ").append(escape_html((String) payload_map.get("newStatus"))).append(")")
                                .append("</td></tr>");

                        sb.append("<tr>")
                                .append("<th class=\"doc-data-label\">사유/비고</th>")
                                .append("<td class=\"doc-data-value\">")
                                .append(escape_html((String) payload_map.get("reason")))
                                .append("</td></tr>");

                        sb.append("</tbody></table>");
                    }
                }
            }

            sb.append("</div>");
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("결재 문서 본문 생성 중 오류가 발생했습니다.", e);
        }
    }
    private String escape_html(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }


}