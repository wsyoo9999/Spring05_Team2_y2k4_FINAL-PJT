package com.multi.y2k4.controller;

import com.multi.y2k4.vo.finance.Documents;
import com.multi.y2k4.vo.finance.Profit;
import com.multi.y2k4.vo.finance.Spend;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private List<Documents> documentList = new ArrayList<>();
    private List<Profit> profitList = new ArrayList<>();
    private List<Spend> spendList = new ArrayList<>();
    private Long docIdSequence = 4L;
    private Long profitIdSequence = 1L;
    private Long spendIdSequence = 1L;

    public FinanceController() {
        // --- 초기 더미 데이터 설정 ---

        // Documents 더미 데이터 (docId 1, 2, 3)
        documentList.add(new Documents(docIdSequence++, "테스트 기안서 (승인 완료)", 100L, LocalDate.now().minusDays(3), "테스트 내용", "APPROVED", LocalDate.now().minusDays(2), 200L));
        documentList.add(new Documents(docIdSequence++, "출장비 정산 (대기 중)", 101L, LocalDate.now().minusDays(1), "출장 정산 요청", "PENDING", null, null));
        documentList.add(new Documents(docIdSequence++, "사무용품 구매 (반려됨)", 100L, LocalDate.now().minusDays(5), "볼펜/용지 구매", "REJECTED", LocalDate.now().minusDays(4), 200L));


        // Profit 더미 데이터 (7번 API listProfits 테스트용)
        profitList.add(new Profit(profitIdSequence++, 101, new BigDecimal("1500000"), LocalDateTime.now().minusDays(5), "제품A 판매"));
        profitList.add(new Profit(profitIdSequence++, 102, new BigDecimal("750000"), LocalDateTime.now().minusDays(2), "서비스 구독료"));

        // Spend 더미 데이터 (8번 API listSpends 테스트용)
        spendList.add(new Spend(spendIdSequence++, 201, new BigDecimal("50000"), LocalDateTime.now().minusDays(4), "사무용품 구매"));
        spendList.add(new Spend(spendIdSequence++, 202, new BigDecimal("120000"), LocalDateTime.now().minusDays(1), "직원 식대"));

        docIdSequence = (long)documentList.size() + 1;
    }

    // ================================================================
    // 1. 결재 문서 관리 (Documents) - 기존 1~6번 API (간소화된 더미)
    // ================================================================

    // [C] 1. 결재 문서 등록
    @PostMapping("/documents")
    public ResponseEntity<Documents> registerDocument(@RequestBody Documents document) {
        document.setDocId(docIdSequence++);
        document.setStatus("PENDING");
        document.setRequestDate(LocalDate.now());
        documentList.add(document);
        System.out.println("결재 문서 등록: ID " + document.getDocId());
        return ResponseEntity.status(201).body(document);
    }

    // [U] 2. 결재 문서 수정
    @PutMapping("/documents/{docId}")
    public ResponseEntity<Documents> updateDocument(@PathVariable Long docId, @RequestBody Documents updatedDoc) {
        Documents targetDoc = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);
        if (targetDoc == null) return ResponseEntity.notFound().build();
        targetDoc.setTitle(updatedDoc.getTitle());
        targetDoc.setContent(updatedDoc.getContent());
        System.out.println("결재 문서 수정: ID " + docId);
        return ResponseEntity.ok(targetDoc);
    }

    // [D] 3. 결재 문서 삭제
    @DeleteMapping("/documents/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long docId) {
        boolean removed = documentList.removeIf(d -> d.getDocId().equals(docId));
        if (removed) {
            System.out.println("결재 문서 삭제: ID " + docId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [R - Detail] 4. 결재 문서 상세 조회
    @GetMapping("/documents/{docId}")
    public ResponseEntity<Documents> getDocument(@PathVariable Long docId) {
        Documents document = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);
        if (document != null) {
            System.out.println("결재 문서 상세 조회: ID " + docId);
            return ResponseEntity.ok(document);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [R - List] 5. 결재 문서 목록 조회
    @GetMapping("/documents")
    public ResponseEntity<List<Documents>> listDocuments(
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false) String status) {

        List<Documents> filteredList = documentList.stream()
                .filter(d -> requesterId == null || d.getRequesterId().equals(requesterId))
                .filter(d -> status == null || d.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        System.out.println("결재 문서 목록 조회 (필터): requesterId=" + requesterId + ", status=" + status);
        return ResponseEntity.ok(filteredList);
    }

    // [U - Status] 6. 결재 상태 변경
    @PutMapping("/documents/{docId}/approval")
    public ResponseEntity<Documents> updateApprovalStatus(
            @PathVariable Long docId,
            @RequestParam String newStatus,
            @RequestParam Long approverId) {

        Documents targetDoc = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);
        if (targetDoc == null) return ResponseEntity.notFound().build();

        if ("APPROVED".equalsIgnoreCase(newStatus) || "REJECTED".equalsIgnoreCase(newStatus)) {
            targetDoc.setStatus(newStatus.toUpperCase());
            targetDoc.setApproverId(approverId);
            targetDoc.setApprovalDate(LocalDate.now());
            System.out.println("결재 상태 변경: ID " + docId + ", 상태: " + newStatus.toUpperCase());
            return ResponseEntity.ok(targetDoc);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // ================================================================
    // 2. 회사 수익 관리 (Profit CRUD) - API 7번 대역
    // ================================================================

    // [C] 7-1. 수익 등록 (POST /api/finance/profit)
    @PostMapping("/profit")
    public ResponseEntity<Profit> registerProfit(@RequestBody Profit profit) {
        profit.setProfitId(profitIdSequence++);
        profit.setProfitDate(LocalDateTime.now());
        profitList.add(profit);
        System.out.println("수익 등록: ID " + profit.getProfitId());
        return ResponseEntity.status(201).body(profit);
    }

    // [R - List] 7-2. 수익 목록 조회 (GET /api/finance/profit)
    @GetMapping("/profit")
    public ResponseEntity<List<Profit>> listProfits(
            @RequestParam(required = false) Integer profitCode,
            @RequestParam(required = false) String searchComment) {

        List<Profit> filteredList = profitList.stream()
                .filter(p -> profitCode == null || p.getProfitCode() == null || p.getProfitCode().equals(profitCode))
                .filter(p -> searchComment == null || p.getProfitComment() == null || p.getProfitComment().contains(searchComment))
                .collect(Collectors.toList());

        System.out.println("수익 목록 조회 (필터): code=" + profitCode + ", comment=" + searchComment);
        return ResponseEntity.ok(filteredList);
    }

    // [R - Detail] 7-3. 수익 상세 조회 (GET /api/finance/profit/{profitId})
    @GetMapping("/profit/{profitId}")
    public ResponseEntity<Profit> getProfit(@PathVariable Long profitId) {
        Profit profit = profitList.stream()
                .filter(p -> p.getProfitId().equals(profitId))
                .findFirst().orElse(null);
        if (profit != null) {
            System.out.println("수익 상세 조회: ID " + profitId);
            return ResponseEntity.ok(profit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [U] 7-4. 수익 수정 (PUT /api/finance/profit/{profitId})
    @PutMapping("/profit/{profitId}")
    public ResponseEntity<Profit> updateProfit(@PathVariable Long profitId, @RequestBody Profit updatedProfit) {
        Profit targetProfit = profitList.stream()
                .filter(p -> p.getProfitId().equals(profitId))
                .findFirst().orElse(null);
        if (targetProfit == null) return ResponseEntity.notFound().build();

        targetProfit.setProfitCode(updatedProfit.getProfitCode());
        targetProfit.setProfit(updatedProfit.getProfit());
        targetProfit.setProfitComment(updatedProfit.getProfitComment());

        System.out.println("수익 수정: ID " + profitId);
        return ResponseEntity.ok(targetProfit);
    }

    // [D] 7-5. 수익 삭제 (DELETE /api/finance/profit/{profitId})
    @DeleteMapping("/profit/{profitId}")
    public ResponseEntity<Void> deleteProfit(@PathVariable Long profitId) {
        boolean removed = profitList.removeIf(p -> p.getProfitId().equals(profitId));
        if (removed) {
            System.out.println("수익 삭제: ID " + profitId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ================================================================
    // 3. 회사 지출 관리 (Spend CRUD) - API 8번 대역
    // ================================================================

    // [C] 8-1. 지출 등록 (POST /api/finance/spend)
    @PostMapping("/spend")
    public ResponseEntity<Spend> registerSpend(@RequestBody Spend spend) {
        spend.setSpendId(spendIdSequence++);
        spend.setSpendDate(LocalDateTime.now());
        spendList.add(spend);
        System.out.println("지출 등록: ID " + spend.getSpendId());
        return ResponseEntity.status(201).body(spend);
    }

    // [R - List] 8-2. 지출 목록 조회 (GET /api/finance/spend)
    @GetMapping("/spend")
    public ResponseEntity<List<Spend>> listSpends(
            @RequestParam(required = false) Integer spendCode,
            @RequestParam(required = false) String searchComment) {

        List<Spend> filteredList = spendList.stream()
                .filter(s -> spendCode == null || s.getSpendCode() == null || s.getSpendCode().equals(spendCode))
                .filter(s -> searchComment == null || s.getSpendComment() == null || s.getSpendComment().contains(searchComment))
                .collect(Collectors.toList());

        System.out.println("지출 목록 조회 (필터): code=" + spendCode + ", comment=" + searchComment);
        return ResponseEntity.ok(filteredList);
    }

    // [R - Detail] 8-3. 지출 상세 조회 (GET /api/finance/spend/{spendId})
    @GetMapping("/spend/{spendId}")
    public ResponseEntity<Spend> getSpend(@PathVariable Long spendId) {
        Spend spend = spendList.stream()
                .filter(s -> s.getSpendId().equals(spendId))
                .findFirst().orElse(null);
        if (spend != null) {
            System.out.println("지출 상세 조회: ID " + spendId);
            return ResponseEntity.ok(spend);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [U] 8-4. 지출 수정 (PUT /api/finance/spend/{spendId})
    @PutMapping("/spend/{spendId}")
    public ResponseEntity<Spend> updateSpend(@PathVariable Long spendId, @RequestBody Spend updatedSpend) {
        Spend targetSpend = spendList.stream()
                .filter(s -> s.getSpendId().equals(spendId))
                .findFirst().orElse(null);
        if (targetSpend == null) return ResponseEntity.notFound().build();

        targetSpend.setSpendCode(updatedSpend.getSpendCode());
        targetSpend.setSpend(updatedSpend.getSpend());
        targetSpend.setSpendComment(updatedSpend.getSpendComment());

        System.out.println("지출 수정: ID " + spendId);
        return ResponseEntity.ok(targetSpend);
    }

    // [D] 8-5. 지출 삭제 (DELETE /api/finance/spend/{spendId})
    @DeleteMapping("/spend/{spendId}")
    public ResponseEntity<Void> deleteSpend(@PathVariable Long spendId) {
        boolean removed = spendList.removeIf(s -> s.getSpendId().equals(spendId));
        if (removed) {
            System.out.println("지출 삭제: ID " + spendId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}