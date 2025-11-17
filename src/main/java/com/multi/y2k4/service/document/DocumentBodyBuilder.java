package com.multi.y2k4.service.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;


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

            StringBuilder content_sb = new StringBuilder();
            content_sb.append("<div class=\"doc-content\">");

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
                if (tb_id == 0) {  //판매

                    if (cd_id == 0 || cd_id == 2) {  //추가 or 삭제
                        Object sale_obj = payload_map.get("sale");
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

                        // 판매 기본 정보 테이블
                        if (sale != null) {
                            sb.append("<h4 class=\"doc-subtitle\">판매 기본 정보</h4>");
                            sb.append("<table class=\"doc-map-table\"><tbody>");

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

                        // 판매 상세 정보 테이블
                        sb.append("<h4 class=\"doc-subtitle\">판매 상세 항목</h4>");
                        if (details_list == null || details_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table class=\"doc-list-table\">")
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

                    } else if (cd_id == 1) {  //수정

                        Object data_obj = payload_map.get("data");
                        List<SaleDetails> before_list = null;
                        List<SaleDetails> after_list  = null;

                        if (data_obj != null) {
                            Map<String, Object> data_map =
                                    objectMapper.convertValue(data_obj, new TypeReference<Map<String, Object>>() {});

                            Object before_obj = data_map.get("before");
                            Object after_obj  = data_map.get("after");

                            if (before_obj != null) {
                                before_list = objectMapper.convertValue(
                                        before_obj, new TypeReference<List<SaleDetails>>() {});
                            }
                            if (after_obj != null) {
                                after_list = objectMapper.convertValue(
                                        after_obj, new TypeReference<List<SaleDetails>>() {});
                            }
                        }

                        sb.append("<h3 class=\"doc-section-title\">판매 상세 수정 내역</h3>");

                        // 변경 전
                        sb.append("<h4 class=\"doc-subtitle\">변경 전 상세 항목</h4>");
                        if (before_list == null || before_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">이전 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table class=\"doc-list-table\">")
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

                        // 변경 후
                        sb.append("<h4 class=\"doc-subtitle\">변경 후 상세 항목</h4>");
                        if (after_list == null || after_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">변경 후 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table class=\"doc-list-table\">")
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

                } else if (tb_id == 1) {    //구매


                    if (cd_id == 0 || cd_id == 2) {  //추가 or 삭제
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
                            sb.append("<table class=\"doc-map-table\"><tbody>");

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
                            sb.append("<table class=\"doc-list-table\">")
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
                                int purchase_qty =d.getPurchase_qty();
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

                    } else if (cd_id == 1) {  //수정


                        Object data_obj = payload_map.get("data");
                        List<PurchaseDetails> before_list = Collections.emptyList();
                        List<PurchaseDetails> after_list  = Collections.emptyList();

                        if (data_obj != null) {
                            Map<String, Object> data_map =
                                    objectMapper.convertValue(data_obj, new TypeReference<Map<String, Object>>() {});

                            Object before_obj = data_map.get("before");
                            Object after_obj  = data_map.get("after");

                            if (before_obj != null) {
                                before_list = objectMapper.convertValue(
                                        before_obj, new TypeReference<List<PurchaseDetails>>() {});
                            }
                            if (after_obj != null) {
                                after_list = objectMapper.convertValue(
                                        after_obj, new TypeReference<List<PurchaseDetails>>() {});
                            }
                        }

                        sb.append("<h3 class=\"doc-section-title\">구매 상세 수정 내역</h3>");

                        // 변경 전
                        sb.append("<h4 class=\"doc-subtitle\">변경 전 상세 항목</h4>");
                        if (before_list == null || before_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">이전 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table class=\"doc-list-table\">")
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

                        // 변경 후
                        sb.append("<h4 class=\"doc-subtitle\">변경 후 상세 항목</h4>");
                        if (after_list == null || after_list.isEmpty()) {
                            sb.append("<p class=\"doc-info\">변경 후 상세 항목이 없습니다.</p>");
                        } else {
                            sb.append("<table class=\"doc-list-table\">")
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

            } else if (cat_id == 2) { // 생산/제조
                if (tb_id == 0) {  //작업 지시서
                    if (cd_id == 0) {  //추가


                    } else if (cd_id == 1) {  //수정


                    } else if (cd_id == 2) {  //삭제


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

            } else if (cat_id == 4) { //인사
                if (tb_id == 0) {  //휴가 및 퇴직 처리
                    if (cd_id == 0) {  //추가


                    } else if (cd_id == 1) {  //수정


                    } else if (cd_id == 2) {  //삭제


                    }
                }
            }

            content_sb.append("</div>");
            return content_sb.toString();

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