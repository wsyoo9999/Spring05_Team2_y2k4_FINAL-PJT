package com.multi.y2k4.controller;

import com.multi.y2k4.vo.finance.Budget;
import com.multi.y2k4.vo.finance.Documents;
import com.multi.y2k4.vo.finance.Slips;
import lombok.AllArgsConstructor; // Lombok AllArgsConstructor import 필요
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private List<Documents> documentList = new ArrayList<>();
    private List<Slips> slipList = new ArrayList<>();
    private List<Budget> budgetList = new ArrayList<>();
    private Long docIdSequence = 1L;
    private Long slipIdSequence = 1L;

    public FinanceController() {
        // --- 초기 더미 데이터 설정 ---

        // Budget 더미 데이터 (7번, 8번, 9번 테스트용)
        budgetList.add(new Budget("401", "복리후생비", new BigDecimal("10000000"), new BigDecimal("9500000")));
        budgetList.add(new Budget("501", "여비교통비", new BigDecimal("5000000"), new BigDecimal("4800000")));

        // Documents 더미 데이터 (1번~6번, 10번 테스트용)
        List<Slips> doc1Slips = List.of(
                new Slips(0L, 1L, null, "401", "복리후생비", new BigDecimal("500000"), new BigDecimal("0"), "간식비 지출", "READY"),
                new Slips(0L, 1L, null, "101", "현금", new BigDecimal("0"), new BigDecimal("500000"), "현금 지출", "READY")
        );
        documentList.add(new Documents(docIdSequence++, "테스트 기안서 (승인 완료)", 100L, LocalDate.now().minusDays(3), "테스트 내용", "APPROVED", LocalDate.now().minusDays(2), 200L, doc1Slips));

        List<Slips> doc2Slips = List.of(
                new Slips(0L, 2L, null, "501", "여비교통비", new BigDecimal("200000"), new BigDecimal("0"), "출장비", "READY"),
                new Slips(0L, 2L, null, "101", "현금", new BigDecimal("0"), new BigDecimal("200000"), "현금 지출", "READY")
        );
        documentList.add(new Documents(docIdSequence++, "출장비 정산 (대기 중)", 101L, LocalDate.now().minusDays(1), "출장 정산 요청", "PENDING", null, null, doc2Slips));
        documentList.add(new Documents(docIdSequence++, "사무용품 구매 (반려됨)", 100L, LocalDate.now().minusDays(5), "볼펜/용지 구매", "REJECTED", LocalDate.now().minusDays(4), 200L, new ArrayList<>()));

        // Slips 더미 데이터 (11번, 12번 테스트용) -> doc1 기반으로 미리 생성된 전표 가정
        slipList.add(new Slips(slipIdSequence++, 1L, LocalDate.now().minusDays(2), "401", "복리후생비", new BigDecimal("500000"), new BigDecimal("0"), "간식비 지출", "TRANSFERED"));
        slipList.add(new Slips(slipIdSequence++, 1L, LocalDate.now().minusDays(2), "101", "현금", new BigDecimal("0"), new BigDecimal("500000"), "현금 지출", "TRANSFERED"));

        // --- Sequence 초기화 (실제로는 DB에서 처리됨) ---
        docIdSequence = (long)documentList.size() + 1;
        slipIdSequence = (long)slipList.size() + 1;
    }

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

    // --- 7. 예산 계정 조회 (GET) ---
    @GetMapping("/budget/accounts/{acctCode}")
    public ResponseEntity<Budget> getBudgetAccount(@PathVariable String acctCode) {
        Budget budget = budgetList.stream()
                .filter(b -> b.getAcctCode().equals(acctCode))
                .findFirst().orElse(null);
        if (budget != null) {
            System.out.println("예산 계정 조회: 코드 " + acctCode);
            return ResponseEntity.ok(budget);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- 8. 예산 잔액 확인 (GET) ---
    @GetMapping("/budget/check")
    public ResponseEntity<Boolean> checkBudgetRemains(
            @RequestParam String acctCode,
            @RequestParam BigDecimal amount) {

        Budget budget = budgetList.stream()
                .filter(b -> b.getAcctCode().equals(acctCode))
                .findFirst().orElse(null);

        if (budget == null) return ResponseEntity.notFound().build();

        boolean isSufficient = budget.getRemains().compareTo(amount) >= 0;
        System.out.println("예산 잔액 확인: 계정 " + acctCode + ", 금액 " + amount + ", 결과: " + isSufficient);
        return ResponseEntity.ok(isSufficient);
    }

    // --- 9. 예산 잔액 업데이트 (PUT) ---
    @PutMapping("/budget/{acctCode}/remains")
    public ResponseEntity<Budget> updateBudgetRemains(
            @PathVariable String acctCode,
            @RequestParam BigDecimal amount,
            @RequestParam String operation) {

        Budget budget = budgetList.stream()
                .filter(b -> b.getAcctCode().equals(acctCode))
                .findFirst().orElse(null);
        if (budget == null) return ResponseEntity.notFound().build();

        if ("DEDUCT".equalsIgnoreCase(operation)) {
            budget.setRemains(budget.getRemains().subtract(amount.abs()));
        } else if ("ADD".equalsIgnoreCase(operation)) {
            budget.setRemains(budget.getRemains().add(amount.abs()));
        } else {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("예산 잔액 업데이트: 계정 " + acctCode + ", 새 잔액: " + budget.getRemains());
        return ResponseEntity.ok(budget);
    }

    // --- 10. 회계 전표 생성 (POST) ---
    @PostMapping("/documents/{docId}/aggregate")
    public ResponseEntity<List<Slips>> createSlipsFromDocument(@PathVariable Long docId) {
        Documents document = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);

        if (document == null || !"APPROVED".equalsIgnoreCase(document.getStatus())) {
            return ResponseEntity.badRequest().build();
        }

        List<Slips> newSlips = new ArrayList<>();
        if (document.getSlipsList() != null && !document.getSlipsList().isEmpty()) {
            for (Slips slip : document.getSlipsList()) {
                Slips newSlip = new Slips(slipIdSequence++, docId, LocalDate.now(), slip.getAcctCode(), slip.getAcctName(), slip.getDebitAmount(), slip.getCreditAmount(), slip.getRemark(), "READY");
                newSlips.add(newSlip);
                slipList.add(newSlip);
            }
            System.out.println("회계 전표 생성 완료: 문서 ID " + docId + ", 전표 수: " + newSlips.size());
            return ResponseEntity.status(201).body(newSlips);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    // --- 11. 회계 전표 상세 조회 (GET) ---
    @GetMapping("/slips/{slipId}")
    public ResponseEntity<Slips> getSlip(@PathVariable Long slipId) {
        Slips slip = slipList.stream()
                .filter(s -> s.getSlipId().equals(slipId))
                .findFirst().orElse(null);
        if (slip != null) {
            System.out.println("회계 전표 상세 조회: ID " + slipId);
            return ResponseEntity.ok(slip);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- 12. ERP 전송 상태 업데이트 (PUT) ---
    @PutMapping("/slips/{slipId}/transfer-status")
    public ResponseEntity<Slips> updateTransferStatus(
            @PathVariable Long slipId,
            @RequestParam String newStatus) {

        Slips targetSlip = slipList.stream()
                .filter(s -> s.getSlipId().equals(slipId))
                .findFirst().orElse(null);

        if (targetSlip == null) return ResponseEntity.notFound().build();

        targetSlip.setTransferStatus(newStatus.toUpperCase());
        System.out.println("ERP 전송 상태 업데이트: 전표 ID " + slipId + ", 상태: " + newStatus.toUpperCase());
        return ResponseEntity.ok(targetSlip);
    }
}