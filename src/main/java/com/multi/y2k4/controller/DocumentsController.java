package com.multi.y2k4.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.hr.AttendanceService;
import com.multi.y2k4.service.hr.EmployeeService;
import com.multi.y2k4.service.production.ProductionService;
import com.multi.y2k4.service.transaction.PurchaseDetailsService;
import com.multi.y2k4.service.transaction.PurchaseService;
import com.multi.y2k4.service.transaction.SaleDetailsService;
import com.multi.y2k4.service.transaction.SaleService;
import com.multi.y2k4.service.production.ProductionService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import com.multi.y2k4.vo.production.WorkOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doc/")
@RequiredArgsConstructor
public class DocumentsController {
    private final DocumentsService documentsService;
    private final ObjectMapper objectMapper;

    /********판매 구매 관련 서비스*************/
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final SaleDetailsService saleDetailsService;
    private final PurchaseDetailsService purchaseDetailsService;

    /*****************생산 제조 관련 서비스************************/
    private final ProductionService productionService;

    /***********************인사관련 서비스**************************/
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;



    @GetMapping("/list")
    public List<Documents> list(@RequestParam(required = false) Integer doc_id,
                                @RequestParam(required = false) Integer cat_id,
                                @RequestParam(required = false) Integer tb_id,
                                @RequestParam(required = false) Integer cd_id,
                                @RequestParam(required = false) Integer req_id,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate req_date,
                                @RequestParam(required = false) Integer appr_id,
                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appr_date,
                                @RequestParam(required = false) Integer status) {
        return documentsService.list(doc_id, cat_id, tb_id, cd_id, req_id, req_date, appr_id, appr_date, status);
    }

    @GetMapping("/searchById")
    public Documents searchById(@RequestParam Integer doc_id){
        return documentsService.searchById(doc_id);
    }



    @PostMapping("/editStatus")     //결재 승인 및 반려 처리
    public boolean editStatus(@RequestParam Integer doc_id,
                              @RequestParam Integer status) {
        Documents doc = documentsService.searchById(doc_id);
        String query = doc.getQuery();
        HashMap<String, Object> map = null;
        try {
            map = objectMapper.readValue(query, HashMap.class);
            int cat_id = (int) map.get("cat_id");
            int tb_id = (int) map.get("tb_id");
            int cd_id = (int) map.get("cd_id");
            /*===============================재무관련=================================================*/
            if (cat_id == 0) {    //  재무 관련 문서

                if (tb_id == 0) {  //회사 수익 관리

                    if (cd_id == 0) {  //추가
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                        } else if (status == 2) {  //결재 서류 반려


                        }

                    } else if (cd_id == 1) {  //수정
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                        } else if (status == 2) {  //결재 서류 반려


                        }

                    } else if (cd_id == 2) {  //삭제
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                        } else if (status == 2) {  //결재 서류 반려

                        }


                    }

                } else if (tb_id == 1) {    //회사 지출 관리
                    if (cd_id == 0) {  //추가
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                        } else if (status == 2) {  //결재 서류 반려


                        }

                    } else if (cd_id == 1) {  //수정
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                        } else if (status == 2) {  //결재 서류 반려


                        }

                    } else if (cd_id == 2) {  //삭제
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                        } else if (status == 2) {  //결재 서류 반려

                        }


                    }
                }

            /*===============================판매 구매 관련=================================================*/

            } else if (cat_id == 1) { //판매 및 구매 관련
                if (tb_id == 0) {  //판매

                    if (cd_id == 0) {  //추가
                        int pk = Integer.parseInt(map.get("pk").toString());
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
                            Sale s =new Sale();
                            s.setStatus(0);
                            s.setSale_id(pk);
                            saleService.editSaleStatus(s);

                        } else if (status == 2) {  //결재 서류 반려
                            saleService.deleteSale(pk);

                        }

                    } else if (cd_id == 1) {  //수정

                        int before_pk = Integer.parseInt(map.get("before_pk").toString());

                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미

                            Object sale_obj = map.get("after_sale");
                            Object details_obj = map.get("after_details");

                            Sale sale = (sale_obj != null)  //수정할 값
                                    ? objectMapper.convertValue(sale_obj, Sale.class)
                                    : null;

                            List<SaleDetails> details_list = (details_obj != null)  //수정할(기존의 것 삭제하고 다시 넣을) 세부 정보들
                                    ? objectMapper.convertValue(details_obj, new TypeReference<List<SaleDetails>>() {})
                                    : Collections.emptyList();


                            saleDetailsService.deleteSaleDetails(before_pk);   //기존의 판매 세부 정보 삭제

                            saleDetailsService.addSaleDetails(details_list);

                            sale.setSale_id(before_pk);  // pk는 기존 id 고정
                            saleService.editSale(sale);


                            // 3) 상태 되돌리기 (99 -> 0)
                            Sale sStatus = new Sale();
                            sStatus.setSale_id(before_pk);
                            sStatus.setStatus(0);
                            saleService.editSaleStatus(sStatus);  //수정 완료되었다고 status 변경

                        } else if (status == 2) {  //결재 서류 반려,기존의 정보 유지
                            Sale s =new Sale();
                            s.setStatus(0);
                            s.setSale_id(before_pk);
                            saleService.editSaleStatus(s);  //기존의 정보를 유지
                        }

                    } else if (cd_id == 2) {  //삭제
                        int pk = Integer.parseInt(map.get("pk").toString());
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
                            saleService.deleteSale(pk);

                        } else if (status == 2) {  //결재 서류 반려
                            Sale s =new Sale();
                            s.setStatus(0);
                            s.setSale_id(pk);
                            saleService.editSaleStatus(s);  //삭제 거부, 기존의 데이터 유지
                        }

                    }

                } else if (tb_id == 1) {    //구매

                    if (cd_id == 0) {  //추가
                        int pk = Integer.parseInt(map.get("pk").toString());
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
                            Purchase p = new Purchase();
                            p.setStatus(0);
                            p.setPurchase_id(pk);
                            purchaseService.editPurchaseStatus(p);

                        } else if (status == 2) {  //결재 서류 반려
                            purchaseService.deletePurchase(pk); //미리 집어넣은 데이터 삭제
                        }

                    } else if (cd_id == 1) {  //수정

                        int before_pk = Integer.parseInt(map.get("before_pk").toString());

                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
                            Object purchase_obj = map.get("after_purchase");
                            Object details_obj = map.get("after_details");

                            Purchase purchase = (purchase_obj != null)  //수정할 값
                                    ? objectMapper.convertValue(purchase_obj, Purchase.class)
                                    : null;

                            List<PurchaseDetails> details_list = (details_obj != null)  //수정할(기존의 것 삭제하고 다시 넣을) 세부 정보들
                                    ? objectMapper.convertValue(details_obj, new TypeReference<List<PurchaseDetails>>() {})
                                    : Collections.emptyList();


                            purchaseDetailsService.deletePurchaseDetails(before_pk);   //기존의 판매 세부 정보 삭제

                            purchaseDetailsService.addPurchaseDetails(details_list);


                            purchase.setPurchase_id(before_pk);
                            purchaseService.editPurchase(purchase);  // emp, ac, 날짜, total_price 수정


                            // 3) status 0으로
                            Purchase pStatus = new Purchase();
                            pStatus.setPurchase_id(before_pk);
                            pStatus.setStatus(0);
                            purchaseService.editPurchaseStatus(pStatus);

                        } else if (status == 2) {  //결재 서류 반려
                            Purchase p =new Purchase();
                            p.setStatus(0);
                            p.setPurchase_id(before_pk);
                            purchaseService.editPurchaseStatus(p);  //기존의 정보를 유지
                        }

                    } else if (cd_id == 2) {  //삭제
                        int pk = Integer.parseInt(map.get("pk").toString());
                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
                            purchaseService.deletePurchase(pk);

                        } else if (status == 2) {  //결재 서류 반려
                            Purchase p = new Purchase();
                            p.setStatus(0);
                            p.setPurchase_id(pk);
                            purchaseService.editPurchaseStatus(p);    //삭제 반려, 기존의 값 유지
                        }

                    }
                }

            /*===============================생산 제조 관련=================================================*/

            } else if (cat_id == 2) { // 생산/제조
                if (tb_id == 0) {  // 작업 지시서

                    // JSON에서 workOrder 객체 추출 (추가/삭제 공통 사용)
                    Object workOrderObj = map.get("workOrder");
                    WorkOrder workOrder = (workOrderObj != null)
                            ? objectMapper.convertValue(workOrderObj, WorkOrder.class)
                            : null;

                    if (cd_id == 0) {  // [추가] 요청 처리
                        if (status == 1) {    // 1: 승인 -> DB에 실제 데이터 INSERT
                            if (workOrder != null) {
                                // 결재 승인 시 상태를 '대기(0)' 등으로 확정하여 저장
                                // (ProductionController에서 임시로 저장하지 않았다면 여기서 저장)
                                productionService.addWorkOrder(workOrder);
                            }
                        } else if (status == 2) {
                            // 반려 시에는 DB에 넣지 않음 (별도 처리 없음)
                        }


                    } else if (cd_id == 2) {  // [삭제] 요청 처리
                        if (status == 1) {    // 1: 승인 -> DB 데이터 DELETE
                            if (workOrder != null && workOrder.getWork_order_id() != null) {
                                // 실제 DB에서 삭제 수행
                                productionService.deleteWorkOrder(workOrder.getWork_order_id());
                            }
                        }
                        // 반려 시에는 삭제하지 않고 유지
                    }
                }


            /*===============================재고 관련=================================================*/

//            } else if (cat_id == 3) { //재고
//                if (tb_id == 0) {  //추후 확장을 위함
//                    if (cd_id == 0) {  //추가
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//
//                        } else if (status == 2) {  //결재 서류 반려
//
//                        }
//
//                    } else if (cd_id == 1) {  //수정
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//
//                        } else if (status == 2) {  //결재 서류 반려
//
//                        }
//
//                    } else if (cd_id == 2) {  //삭제
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//
//                        } else if (status == 2) {  //결재 서류 반려
//
//                        }
//
//                    }
//                }

                /*===============================인사 관련=================================================*/

            } if (cat_id == 4) { // 인사
                if (tb_id == 0) {  // 휴가 및 퇴직 처리
                    if (cd_id == 0) {  // [추가] 휴가 신청 처리

                        if (status == 1) { // [승인] 시 실제 DB 반영

                            Integer reqId = doc.getReq_id().intValue();
                            LocalDate startDate = LocalDate.parse((String) map.get("startDate"));
                            LocalDate endDate = LocalDate.parse((String) map.get("endDate"));

                            // 2. 휴가 기간만큼 근태 기록 생성
                            java.util.List<com.multi.y2k4.vo.hr.Attendance> vacationList = new java.util.ArrayList<>();

                            // 시작일부터 종료일까지 하루씩 증가하며 반복
                            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                                com.multi.y2k4.vo.hr.Attendance att = new com.multi.y2k4.vo.hr.Attendance();
                                att.setEmp_id(reqId);
                                att.setWork_date(date);
                                att.setCheck_in(date.atTime(9, 0));
                                att.setCheck_out(date.atTime(18, 0));
                                att.setAttendance_status("휴가"); // ★ 상태를 '휴가'로 설정

                                vacationList.add(att);
                            }

                            // 3. 근태 테이블에 일괄 반영 (기존 기록이 있으면 덮어쓰기)
                            if (!vacationList.isEmpty()) {
                                attendanceService.generateDailyAttendance(); // (선택) 혹시 데이터가 없으면 생성 보장

                            }

                        } else if (status == 2) { // [반려]
                            // 반려 시 별도 DB 작업 없음 (문서 상태만 '반려'로 변경됨)
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        return true;
    }




}