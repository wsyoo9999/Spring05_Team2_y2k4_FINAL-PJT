package com.multi.y2k4.controller;

import com.multi.y2k4.vo.finance.Documents;
import com.multi.y2k4.vo.finance.Profit;
import com.multi.y2k4.vo.finance.Spend;
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
    private Long docIdSequence = 1L;
    private Long profitIdSequence = 1L;
    private Long spendIdSequence = 1L;

    public FinanceController() {
        // --- 초기 더미 데이터 설정 ---

        // Documents 더미 데이터 (docId 1, 2, 3)
        documentList.add(new Documents(docIdSequence++, "테스트 기안서 (승인 완료)", 100L, LocalDate.now().minusDays(3), "테스트 내용", "APPROVED", LocalDate.now().minusDays(2), 200L));
        documentList.add(new Documents(docIdSequence++, "출장비 정산 (대기 중)", 101L, LocalDate.now().minusDays(1), "출장 정산 요청", "PENDING", null, null));
        documentList.add(new Documents(docIdSequence++, "사무용품 구매 (반려됨)", 100L, LocalDate.now().minusDays(5), "볼펜/용지 구매", "REJECTED", LocalDate.now().minusDays(4), 200L));

        // Profit 더미 데이터 (수익 관리 테스트용)
        profitList.add(new Profit(profitIdSequence++, 101, new BigDecimal("1500000"), LocalDateTime.now().minusDays(5), "제품A 판매 대금"));
        profitList.add(new Profit(profitIdSequence++, 102, new BigDecimal("750000"), LocalDateTime.now().minusDays(2), "서비스 구독료 입금"));

        // Spend 더미 데이터 (지출 관리 테스트용)
        spendList.add(new Spend(spendIdSequence++, 201, new BigDecimal("50000"), LocalDateTime.now().minusDays(4), "사무용품 구매"));
        spendList.add(new Spend(spendIdSequence++, 202, new BigDecimal("120000"), LocalDateTime.now().minusDays(1), "직원 식대 지출"));

        // --- Sequence 초기화 ---
        docIdSequence = (long)documentList.size() + 1;
        profitIdSequence = (long)profitList.size() + 1;
        spendIdSequence = (long)spendList.size() + 1;
    }

    // ================================================================
    // 1~6. 결재 문서 관리 API (유지)
    // ================================================================

    // --- 1. 결재 문서 등록 (POST) ---
    @PostMapping("/documents")
    public ResponseEntity<Documents> registerDocument(@RequestBody Documents document) {
        document.setDocId(docIdSequence++);
        document.setStatus("PENDING");
        document.setRequestDate(LocalDate.now());
        documentList.add(document);
        System.out.println("결재 문서 등록: ID " + document.getDocId());
        return ResponseEntity.status(201).body(document);
    }

    // --- 2. 결재 문서 수정 (PUT) ---
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

    // --- 3. 결재 문서 삭제 (DELETE) ---
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

    // --- 4. 결재 문서 상세 조회 (GET) ---
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

    // --- 5. 결재 문서 목록 조회 (GET) ---
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

    // --- 6. 결재 상태 변경 (승인/반려) (PUT) ---
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
    // 7. 회사 수익 목록 조회 (GET /api/finance/profit)
    // ================================================================
    @GetMapping("/profit")
    public ResponseEntity<List<Profit>> listProfits(
            @RequestParam(required = false) Integer profitCode,
            @RequestParam(required = false) String searchComment) {

        List<Profit> filteredList = profitList.stream()
                .filter(p -> profitCode == null || p.getProfitCode().equals(profitCode))
                .filter(p -> searchComment == null || p.getProfitComment().contains(searchComment))
                .collect(Collectors.toList());

        System.out.println("수익 목록 조회 (필터): code=" + profitCode + ", comment=" + searchComment);
        return ResponseEntity.ok(filteredList);
    }

    // ================================================================
    // 8. 회사 지출 목록 조회 (GET /api/finance/spend)
    // ================================================================
    @GetMapping("/spend")
    public ResponseEntity<List<Spend>> listSpends(
            @RequestParam(required = false) Integer spendCode,
            @RequestParam(required = false) String searchComment) {

        List<Spend> filteredList = spendList.stream()
                .filter(s -> spendCode == null || s.getSpendCode().equals(spendCode))
                .filter(s -> searchComment == null || s.getSpendComment().contains(searchComment))
                .collect(Collectors.toList());

        System.out.println("지출 목록 조회 (필터): code=" + spendCode + ", comment=" + searchComment);
        return ResponseEntity.ok(filteredList);
    }
}