package com.multi.y2k4.controller;

import com.multi.y2k4.service.finance.DocumentService;
import com.multi.y2k4.service.finance.ProfitService;
import com.multi.y2k4.service.finance.SpendService;
import com.multi.y2k4.vo.finance.Documents;
import com.multi.y2k4.vo.finance.Profit;
import com.multi.y2k4.vo.finance.Spend;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FinanceController {

    // Service 계층 주입 (DB SELECT 시 사용)
    private final DocumentService documentService;
    private final ProfitService profitService;
    private final SpendService spendService;

    // --- DB 연동 실패 시 Fallback 및 임시 CRUD 처리를 위한 인메모리 리스트 ---

    private List<Documents> documentList = new ArrayList<>(List.of(
            new Documents(1L, "테스트 기안서 (승인 완료)", 100L, LocalDate.now().minusDays(3), "테스트 내용", "APPROVED", LocalDate.now().minusDays(2), 200L),
            new Documents(2L, "출장비 정산 (대기 중)", 101L, LocalDate.now().minusDays(1), "출장 정산 요청", "PENDING", null, null),
            new Documents(3L, "사무용품 구매 (반려됨)", 100L, LocalDate.now().minusDays(5), "볼펜/용지 구매", "REJECTED", LocalDate.now().minusDays(4), 200L)
    ));

    private List<Profit> profitList = new ArrayList<>(List.of(
            new Profit(1L, 101, new BigDecimal("1500000"), LocalDateTime.now().minusDays(5), "제품A 판매"),
            new Profit(2L, 102, new BigDecimal("750000"), LocalDateTime.now().minusDays(2), "서비스 구독료")
    ));

    private List<Spend> spendList = new ArrayList<>(List.of(
            new Spend(1L, 201, new BigDecimal("50000"), LocalDateTime.now().minusDays(4), "사무용품 구매"),
            new Spend(2L, 202, new BigDecimal("120000"), LocalDateTime.now().minusDays(1), "직원 식대")
    ));

    // 시퀀스 초기값 설정 (더미 데이터 수 이후부터 시작)
    private Long docIdSequence = (long)documentList.size() + 1;
    private Long profitIdSequence = (long)profitList.size() + 1;
    private Long spendIdSequence = (long)spendList.size() + 1;


    // ================================================================
    // 1. 결재 문서 관리 (Documents) - SELECT DB 연동 + CRUD Fallback
    // ================================================================

    // [C] 1. 결재 문서 등록 (인메모리 List 사용)
    @PostMapping("/documents")
    public ResponseEntity<Documents> registerDocument(@RequestBody Documents document) {
        document.setDocId(docIdSequence++);
        document.setStatus("PENDING");
        document.setRequestDate(LocalDate.now());
        documentList.add(document);
        System.out.println("결재 문서 등록 (더미): ID " + document.getDocId());
        return ResponseEntity.status(201).body(document);
    }

    // [U] 2. 결재 문서 수정 (인메모리 List 사용)
    @PutMapping("/documents/{docId}")
    public ResponseEntity<Documents> updateDocument(@PathVariable Long docId, @RequestBody Documents updatedDoc) {
        // 💡 DB 연동 시: documentService.updateDocument(docId, updatedDoc);
        Documents targetDoc = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);
        if (targetDoc == null) return ResponseEntity.notFound().build();
        targetDoc.setTitle(updatedDoc.getTitle());
        targetDoc.setContent(updatedDoc.getContent());
        System.out.println("결재 문서 수정 (더미): ID " + docId);
        return ResponseEntity.ok(targetDoc);
    }

    // [D] 3. 결재 문서 삭제 (인메모리 List 사용)
    @DeleteMapping("/documents/{docId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long docId) {
        // 💡 DB 연동 시: documentService.deleteDocument(docId);
        boolean removed = documentList.removeIf(d -> d.getDocId().equals(docId));
        if (removed) {
            System.out.println("결재 문서 삭제 (더미): ID " + docId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [R - Detail] 4. 결재 문서 상세 조회 (인메모리 List 사용)
    @GetMapping("/documents/{docId}")
    public ResponseEntity<Documents> getDocument(@PathVariable Long docId) {
        // 💡 DB 연동 시: documentService.getDocument(docId);
        Documents document = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);
        if (document != null) {
            System.out.println("결재 문서 상세 조회 (더미): ID " + docId);
            return ResponseEntity.ok(document);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // [R - List] 5. 결재 문서 목록 조회 (DB SELECT + Fallback)
    @GetMapping("/documents")
    public ResponseEntity<List<Documents>> listDocuments(
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false) String status) {

        List<Documents> documents = null;
        try {
            // 1. DB에서 데이터 조회 시도
            documents = documentService.listDocuments(requesterId, status);
        } catch (Exception e) {
            System.out.println("DB 연결/조회 실패: " + e.getMessage());
        }

        // 2. DB 연동이 실패했거나 데이터가 없을 경우 더미 데이터 사용 (Fallback)
        if (documents == null || documents.isEmpty()) {
            System.out.println("DB 데이터가 없거나 조회 실패. 인메모리 더미 데이터 사용.");
            // 필터링은 인메모리 리스트에서 수행
            documents = documentList.stream()
                    .filter(d -> requesterId == null || d.getRequesterId() == null || d.getRequesterId().equals(requesterId))
                    .filter(d -> status == null || d.getStatus() == null || d.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        System.out.println("결재 문서 목록 조회 (결과): size=" + documents.size());
        return ResponseEntity.ok(documents);
    }

    // [U - Status] 6. 결재 상태 변경 (인메모리 List 사용)
    @PutMapping("/documents/{docId}/approval")
    public ResponseEntity<Documents> updateApprovalStatus(
            @PathVariable Long docId,
            @RequestParam String newStatus,
            @RequestParam Long approverId) {

        // 💡 DB 연동 시: documentService.updateApprovalStatus(docId, newStatus, approverId);
        Documents targetDoc = documentList.stream()
                .filter(d -> d.getDocId().equals(docId))
                .findFirst().orElse(null);
        if (targetDoc == null) return ResponseEntity.notFound().build();

        if ("APPROVED".equalsIgnoreCase(newStatus) || "REJECTED".equalsIgnoreCase(newStatus)) {
            targetDoc.setStatus(newStatus.toUpperCase());
            targetDoc.setApproverId(approverId);
            targetDoc.setApprovalDate(LocalDate.now());
            System.out.println("결재 상태 변경 (더미): ID " + docId + ", 상태: " + newStatus.toUpperCase());
            return ResponseEntity.ok(targetDoc);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // ================================================================
    // 2. 회사 수익 관리 (Profit CRUD) - SELECT DB 연동 + CRUD Fallback
    // ================================================================

    // [C] 7-1. 수익 등록 (인메모리 List 사용)
    @PostMapping("/profit")
    public ResponseEntity<Profit> registerProfit(@RequestBody Profit profit) {
        // 💡 DB 연동 시: profitService.registerProfit(profit);
        profit.setProfitId(profitIdSequence++);
        profit.setProfitDate(LocalDateTime.now());
        profitList.add(profit);
        System.out.println("수익 등록 (더미): ID " + profit.getProfitId());
        return ResponseEntity.status(201).body(profit);
    }

    // [R - List] 7-2. 수익 목록 조회 (DB SELECT + Fallback)
    @GetMapping("/profit")
    public ResponseEntity<List<Profit>> listProfits(
            @RequestParam(required = false) Integer profitCode,
            @RequestParam(required = false) String searchComment) {

        List<Profit> profits = null;
        try {
            // 1. DB에서 데이터 조회 시도
            profits = profitService.listProfits(profitCode, searchComment);
        } catch (Exception e) {
            System.out.println("DB 연결/조회 실패: " + e.getMessage());
        }

        // 2. DB 연동이 실패했거나 데이터가 없을 경우 더미 데이터 사용 (Fallback)
        if (profits == null || profits.isEmpty()) {
            System.out.println("DB 데이터가 없거나 조회 실패. 인메모리 더미 데이터 사용.");
            // 필터링은 인메모리 리스트에서 수행
            profits = profitList.stream()
                    .filter(p -> profitCode == null || p.getProfitCode() == null || p.getProfitCode().equals(profitCode))
                    .filter(p -> searchComment == null || p.getProfitComment() == null || p.getProfitComment().contains(searchComment))
                    .collect(Collectors.toList());
        }

        System.out.println("수익 목록 조회 (결과): size=" + profits.size());
        return ResponseEntity.ok(profits);
    }

    // [R - Detail] 7-3. 수익 상세 조회 (인메모리 List 사용)
    @GetMapping("/profit/{profitId}")
    public ResponseEntity<Profit> getProfit(@PathVariable Long profitId) {
        // 💡 DB 연동 시: profitService.getProfit(profitId);
        Profit profit = profitList.stream()
                .filter(p -> p.getProfitId().equals(profitId))
                .findFirst().orElse(null);

        if (profit == null) return ResponseEntity.notFound().build();
        System.out.println("수익 상세 조회 (더미): ID " + profitId);
        return ResponseEntity.ok(profit);
    }

    // [U] 7-4. 수익 수정 (인메모리 List 사용)
    @PutMapping("/profit/{profitId}")
    public ResponseEntity<Profit> updateProfit(@PathVariable Long profitId, @RequestBody Profit updatedProfit) {
        // 💡 DB 연동 시: profitService.updateProfit(updatedProfit);
        Profit targetProfit = profitList.stream()
                .filter(p -> p.getProfitId().equals(profitId))
                .findFirst().orElse(null);

        if (targetProfit == null) return ResponseEntity.notFound().build();

        targetProfit.setProfitCode(updatedProfit.getProfitCode());
        targetProfit.setProfit(updatedProfit.getProfit());
        targetProfit.setProfitComment(updatedProfit.getProfitComment());

        System.out.println("수익 수정 (더미): ID " + profitId);
        return ResponseEntity.ok(targetProfit);
    }

    // [D] 7-5. 수익 삭제 (인메모리 List 사용)
    @DeleteMapping("/profit/{profitId}")
    public ResponseEntity<Void> deleteProfit(@PathVariable Long profitId) {
        // 💡 DB 연동 시: profitService.deleteProfit(profitId);
        boolean removed = profitList.removeIf(p -> p.getProfitId().equals(profitId));

        if (removed) {
            System.out.println("수익 삭제 (더미): ID " + profitId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ================================================================
    // 3. 회사 지출 관리 (Spend CRUD) - SELECT DB 연동 + CRUD Fallback
    // ================================================================

    // [C] 8-1. 지출 등록 (인메모리 List 사용)
    @PostMapping("/spend")
    public ResponseEntity<Spend> registerSpend(@RequestBody Spend spend) {
        // 💡 DB 연동 시: spendService.registerSpend(spend);
        spend.setSpendId(spendIdSequence++);
        spend.setSpendDate(LocalDateTime.now());
        spendList.add(spend);
        System.out.println("지출 등록 (더미): ID " + spend.getSpendId());
        return ResponseEntity.status(201).body(spend);
    }

    // [R - List] 8-2. 지출 목록 조회 (DB SELECT + Fallback)
    @GetMapping("/spend")
    public ResponseEntity<List<Spend>> listSpends(
            @RequestParam(required = false) Integer spendCode,
            @RequestParam(required = false) String searchComment) {

        List<Spend> spends = null;
        try {
            // 1. DB에서 데이터 조회 시도
            spends = spendService.listSpends(spendCode, searchComment);
        } catch (Exception e) {
            System.out.println("DB 연결/조회 실패: " + e.getMessage());
        }

        // 2. DB 연동이 실패했거나 데이터가 없을 경우 더미 데이터 사용 (Fallback)
        if (spends == null || spends.isEmpty()) {
            System.out.println("DB 데이터가 없거나 조회 실패. 인메모리 더미 데이터 사용.");
            // 필터링은 인메모리 리스트에서 수행
            spends = spendList.stream()
                    .filter(s -> spendCode == null || s.getSpendCode() == null || s.getSpendCode().equals(spendCode))
                    .filter(s -> searchComment == null || s.getSpendComment() == null || s.getSpendComment().contains(searchComment))
                    .collect(Collectors.toList());
        }

        System.out.println("지출 목록 조회 (결과): size=" + spends.size());
        return ResponseEntity.ok(spends);
    }

    // [R - Detail] 8-3. 지출 상세 조회 (인메모리 List 사용)
    @GetMapping("/spend/{spendId}")
    public ResponseEntity<Spend> getSpend(@PathVariable Long spendId) {
        // 💡 DB 연동 시: spendService.getSpend(spendId);
        Spend spend = spendList.stream()
                .filter(s -> s.getSpendId().equals(spendId))
                .findFirst().orElse(null);

        if (spend == null) return ResponseEntity.notFound().build();
        System.out.println("지출 상세 조회 (더미): ID " + spendId);
        return ResponseEntity.ok(spend);
    }

    // [U] 8-4. 지출 수정 (인메모리 List 사용)
    @PutMapping("/spend/{spendId}")
    public ResponseEntity<Spend> updateSpend(@PathVariable Long spendId, @RequestBody Spend updatedSpend) {
        // 💡 DB 연동 시: spendService.updateSpend(updatedSpend);
        Spend targetSpend = spendList.stream()
                .filter(s -> s.getSpendId().equals(spendId))
                .findFirst().orElse(null);

        if (targetSpend == null) return ResponseEntity.notFound().build();

        targetSpend.setSpendCode(updatedSpend.getSpendCode());
        targetSpend.setSpend(updatedSpend.getSpend());
        targetSpend.setSpendComment(updatedSpend.getSpendComment());

        System.out.println("지출 수정 (더미): ID " + spendId);
        return ResponseEntity.ok(targetSpend);
    }

    // [D] 8-5. 지출 삭제 (인메모리 List 사용)
    @DeleteMapping("/spend/{spendId}")
    public ResponseEntity<Void> deleteSpend(@PathVariable Long spendId) {
        // 💡 DB 연동 시: spendService.deleteSpend(spendId);
        boolean removed = spendList.removeIf(s -> s.getSpendId().equals(spendId));

        if (removed) {
            System.out.println("지출 삭제 (더미): ID " + spendId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}