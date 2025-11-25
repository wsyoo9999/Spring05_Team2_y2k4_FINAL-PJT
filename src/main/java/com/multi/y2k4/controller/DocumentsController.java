package com.multi.y2k4.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multi.y2k4.service.alert.AlertService;
import com.multi.y2k4.service.document.DocumentsService;
import com.multi.y2k4.service.finance.UnpaidService;
import com.multi.y2k4.service.hr.AttendanceService;
import com.multi.y2k4.service.hr.EmployeeService;
import com.multi.y2k4.service.inventory.StockService;
import com.multi.y2k4.service.production.ProductionService;
import com.multi.y2k4.service.transaction.*;
import com.multi.y2k4.service.production.ProductionService;
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.finance.Unpaid;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import com.multi.y2k4.vo.production.WorkOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

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
    private final TransactionService transactionService;

    /*****************생산 제조 관련 서비스************************/
    private final ProductionService productionService;

    /***********************인사관련 서비스**************************/
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final UnpaidService unpaidService;

    /*************재고 수정 관련**********/
    private final StockService stockService;
    private final AlertService alertService;

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
        return documentsService.list(doc_id, cat_id, tb_id, cd_id, req_id, req_date, appr_id, appr_date, status, null);
    }

    @GetMapping("/mylist")
    public List<Documents> mylist(
            @RequestParam(required = false) Integer doc_id,
            @RequestParam(required = false) Integer cat_id,
            @RequestParam(required = false) Integer tb_id,
            @RequestParam(required = false) Integer cd_id,
            @RequestParam(required = false) Integer req_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate req_date,
            @RequestParam(required = false) Integer appr_id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appr_date,
            @RequestParam(required = false) Integer status,
            HttpSession session) {

        Integer emp_id = (Integer) session.getAttribute("emp_id");
        if (emp_id == null) {
            return Collections.emptyList();
        }

        // 서비스 호출 시 검색 조건들과 함께 본인 ID(emp_id)를 member_id로 전달
        return documentsService.list(doc_id, cat_id, tb_id, cd_id, req_id, req_date, appr_id, appr_date, status, emp_id);
    }

    @GetMapping("/pending-approvals")
    public List<Documents> pendingApprovals(HttpSession session) {
        Integer emp_id = (Integer) session.getAttribute("emp_id");
        if (emp_id == null) {
            return Collections.emptyList();
        }

        // 서비스의 list 메서드 호출:
        // appr_id = emp_id (본인), status = 0 (대기)
        // 나머지 필터는 null
        return documentsService.list(null, null, null, null, null, null, emp_id, null, 0, null);
    }

    @GetMapping("/searchById")
    public Documents searchById(@RequestParam Integer doc_id){
        return documentsService.searchById(doc_id);
    }

    @GetMapping("/read")
    public void read(@RequestParam Integer doc_id, HttpSession session) {
        documentsService.read(doc_id);
        Integer emp_id = (Integer) session.getAttribute("emp_id");
        alertService.notifyDocCountChanged(emp_id.longValue());
    }



    @PostMapping("/editStatus")     //결재 승인 및 반려 처리
    public boolean editStatus(@RequestParam Integer doc_id,
                              @RequestParam Integer status,
                              @RequestParam(required = false) String comments,HttpSession session) {
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

            } else if (cat_id == 1) {

                transactionService.processSalePurchaseApproval(cat_id, tb_id, cd_id, status, map);

            }
            /*===============================생산 제조 관련=================================================*/
            else if (cat_id == 2) { // 생산/제조
                if (tb_id == 0) {  // 작업 지시서

                    // JSON에서 workOrder 객체 추출 (추가/삭제 공통 사용)
                    Object workOrderObj = map.get("workOrder");
                    WorkOrder workOrder = (workOrderObj != null)
                            ? objectMapper.convertValue(workOrderObj, WorkOrder.class)
                            : null;

                    if (cd_id == 0) {  // [추가] 요청 처리
                        // JSON에서 저장해둔 PK 추출
                        int pk = Integer.parseInt(map.get("pk").toString());

                        if (status == 1) {
                            // [승인]
                            // 1. 상태를 '0'(대기)으로 변경하여 활성화
                            productionService.updateWorkOrderStatus((long) pk, 0);

                            // 2. [추가됨] 재고(acquired_qty) 변동 사항 반영
                            productionService.confirmWorkOrderCreation((long) pk);

                        } else if (status == 2) {
                            // [반려] 임시 저장된 데이터 삭제
                            productionService.deleteWorkOrder((long) pk);
                        }

                    } else if(cd_id == 1) { // [수정] -> 여기서는 '폐기' 상태 변경용
                        int pk = Integer.parseInt(map.get("pk").toString());

                        if (status == 1) { // [승인]
                            // payload에 담긴 newStatus(3:폐기)로 상태 변경
                            if (map.containsKey("newStatus")) {
                                int newStatus = (int) map.get("newStatus");
                                productionService.updateWorkOrderStatus((long)pk, newStatus);

                                // (선택 사항) 폐기 시, 잡아놓았던 재고(acquired_qty)를 해제하려면
                                // 여기서 productionService.releaseStockForDiscard((long)pk) 등을 호출해야 함
                            }
                        }
                        // 반려 시 아무 작업 안 함 (기존 상태 유지)

                    } else if (cd_id == 2) {  // [삭제] 요청 처리
                        // [수정] 추가 처리와 동일하게 pk를 맵에서 직접 꺼내 사용
                        int pk = Integer.parseInt(map.get("pk").toString());

                        if (status == 1) {    // 1: 승인 -> DB 데이터 DELETE
                            // 객체 확인 없이 pk로 바로 삭제
                            productionService.deleteWorkOrder((long)pk);
                        }
                        // 반려(2) 시에는 삭제하지 않고 유지 (별도 로직 없음)
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

            }else if (cat_id == 4) {
                Integer currentEmpId = (Integer) session.getAttribute("emp_id");
                if (currentEmpId == null) return false; // 로그인 안됨

                // 문서에 지정된 결재자(appr_id)와 현재 로그인한 사람(currentEmpId)이 다르면 거부
                if (doc.getAppr_id() == null || doc.getAppr_id().longValue() != currentEmpId.longValue()) {
                    System.out.println("결재 권한 없음: 인사 문서는 지정된 결재자만 처리 가능합니다.");
                    return false;
                }
                if (tb_id == 0) {  // 휴가
                    if (cd_id == 0) {
                        if (status == 1) { // 승인 시 근태 반영
                            Integer reqId = doc.getReq_id().intValue();
                            LocalDate startDate = LocalDate.parse((String) map.get("startDate"));
                            LocalDate endDate = LocalDate.parse((String) map.get("endDate"));
                            attendanceService.applyVacation(reqId, startDate, endDate);
                        }
                    }
                }
                else if (tb_id == 1) { // 퇴직/상태변경
                    if (cd_id == 1) {
                        if (status == 1) { // 승인 시 상태 업데이트
                            int targetEmpId = Integer.parseInt(String.valueOf(map.get("targetEmpId")));
                            String newStatus = (String) map.get("newStatus");

                            com.multi.y2k4.vo.hr.Employee emp = new com.multi.y2k4.vo.hr.Employee();
                            emp.setEmp_id(targetEmpId);
                            emp.setStatus(newStatus);
                            employeeService.updateEmployee(emp);
                        }
                    }
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        documentsService.editStatus(doc_id, status,comments);
        return true;
    }




}