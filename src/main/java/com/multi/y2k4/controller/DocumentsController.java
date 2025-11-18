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
import com.multi.y2k4.vo.document.Documents;
import com.multi.y2k4.vo.transaction.Purchase;
import com.multi.y2k4.vo.transaction.PurchaseDetails;
import com.multi.y2k4.vo.transaction.Sale;
import com.multi.y2k4.vo.transaction.SaleDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
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



    @GetMapping
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



    @PostMapping("/api/doc/editStatus")     //결재 승인 및 반려 처리
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
//                if (tb_id == 0) {  //판매
//
//
//                    if (cd_id == 0) {  //추가
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//                            Sale s =new Sale();
//                            s.setStatus(0);
//                            s.setSale_id(pk);
//                            saleService.editSaleStatus(s);
//
//                        } else if (status == 2) {  //결재 서류 반려
//                            saleService.deleteSale(pk);
//
//                        }
//
//                    } else if (cd_id == 1) {  //수정
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//                            int before_pk = Integer.parseInt(map.get("before_pk").toString());
//                            Object sale_obj = map.get("after_sale");
//                            Object details_obj = map.get("after_details");
//
//                            Sale sale = (sale_obj != null)  //수정할 값
//                                    ? objectMapper.convertValue(sale_obj, Sale.class)
//                                    : null;
//
//                            List<SaleDetails> details_list = (details_obj != null)  //수정할(기존의 것 삭제하고 다시 넣을) 세부 정보들
//                                    ? objectMapper.convertValue(details_obj, new TypeReference<List<SaleDetails>>() {})
//                                    : Collections.emptyList();
//
//
//                            saleDetailsService.deleteSaleDetails(before_pk);   //기존의 판매 세부 정보 삭제
//
//                            saleDetailsService.addSaleDetails(details_list);
//
//                            saleService.editSaleStatus(sale);  //수정 완료되었다고 status 변경
//
//                        } else if (status == 2) {  //결재 서류 반려
//                            Sale s =new Sale();
//                            s.setStatus(0);
//                            s.setSale_id(pk);
//                            saleService.editSaleStatus(s);  //기존의 정보를 유지
//                        }
//
//                    } else if (cd_id == 2) {  //삭제
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//                            saleService.deleteSale(pk);
//
//                        } else if (status == 2) {  //결재 서류 반려
//                            Sale s =new Sale();
//                            s.setStatus(0);
//                            s.setSale_id(pk);
//                            saleService.editSaleStatus(s);  //삭제 거부, 기존의 데이터 유지
//                        }
//
//                    }
//
//                } else if (tb_id == 1) {    //구매
//
//                    if (cd_id == 0) {  //추가
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//
//                            Purchase p = new Purchase();
//                            p.setStatus(0);
//                            p.setPurchase_id(pk);
//                            purchaseService.editPurchase(p);
//
//                        } else if (status == 2) {  //결재 서류 반려
//                            purchaseService.deletePurchase(pk); //미리 집어넣은 데이터 삭제
//                        }
//
//                    } else if (cd_id == 1) {  //수정
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//                            purchaseDetailsService.deletePurchaseDetails(pk);   //기존의 구매 세부 정보 삭제
//                            List<PurchaseDetails> pd = (List<PurchaseDetails>) map.get("data"); //변경할 정보들
//                            purchaseDetailsService.addPurchaseDetails(pd);
//                            Purchase p = new Purchase();
//                            p.setStatus(0);
//                            p.setPurchase_id(pk);
//                            purchaseService.editPurchase(p);
//
//                        } else if (status == 2) {  //결재 서류 반려
//                            Purchase p = new Purchase();
//                            p.setStatus(0);
//                            p.setPurchase_id(pk);
//                            purchaseService.editPurchase(p);    //수정 반려, 기존의 값 유지
//                        }
//
//                    } else if (cd_id == 2) {  //삭제
//                        if (status == 1) {    //결재 문서를 승인으로 변경, 이는 문서 내용을 DB에 반영한다는 의미
//                            purchaseService.deletePurchase(pk);
//
//                        } else if (status == 2) {  //결재 서류 반려
//                            Purchase p = new Purchase();
//                            p.setStatus(0);
//                            p.setPurchase_id(pk);
//                            purchaseService.editPurchase(p);    //삭제 반려, 기존의 값 유지
//                        }
//
//                    }
//                }

            /*===============================생산 제조 관련=================================================*/

            } else if (cat_id == 2) { // 생산/제조
                if (tb_id == 0) {  //작업 지시서
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

            } else if (cat_id == 4) { //인사
                if (tb_id == 0) {  //휴가 및 퇴직 처리
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
            }
        } catch (Exception e) {e.printStackTrace();}
        return true;
    }




}