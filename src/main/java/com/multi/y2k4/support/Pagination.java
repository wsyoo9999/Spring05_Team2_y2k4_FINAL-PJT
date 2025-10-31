package com.multi.y2k4.support;

public class Pagination {
    private int page;       // 현재 페이지
    private int size;       // 한 페이지당 항목 수
    private int total;      // 전체 항목 수
    private int block;      // 한 블록당 페이지 수

    private int start;      // 시작 인덱스
    private int end;        // 끝 인덱스
    private int totalPages; // 전체 페이지 개수
    private int startPage;  // 블록 시작 페이지
    private int endPage;    // 블록 끝 페이지

    public Pagination(int page, int size, int total, int block) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.block = block;

        this.totalPages = (int) Math.ceil((double) total / size);
        if (this.totalPages == 0) this.totalPages = 1;

        this.start = (page - 1) * size + 1;
        this.end = Math.min(start + size - 1, total);

        int currentBlock = (int) Math.ceil((double) page / block);
        this.startPage = (currentBlock - 1) * block + 1;
        this.endPage = Math.min(startPage + block - 1, totalPages);
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public int getTotal() { return total; }
    public int getBlock() { return block; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public int getTotalPages() { return totalPages; }
    public int getStartPage() { return startPage; }
    public int getEndPage() { return endPage; }
    public int getOffset() {
        return (page - 1) * size;
    }
}
