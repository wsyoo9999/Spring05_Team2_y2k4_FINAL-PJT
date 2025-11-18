package com.multi.y2k4.mapper.tenant.finance;

import com.multi.y2k4.vo.finance.Unpaid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UnpaidMapper {

    // cat_id, tb_id, ref_pk 기준으로 upsert(ON DUPLICATE KEY UPDATE)
    int upsertUnpaid(Unpaid unpaid);

    // 특정 비즈니스 건의 미정산 정보 조회
    Unpaid searchByBusiness(
            @Param("cat_id") int cat_id,
            @Param("tb_id") int tb_id,
            @Param("ref_pk") Long ref_pk
    );

    // 단순 PK 조회
    Unpaid searchById(@Param("unp_id") Long unp_id);

    // 필터 리스트 (상태/타입/카테고리)
    List<Unpaid> list(
            @Param("cat_id") Integer cat_id,
            @Param("tb_id") Integer tb_id,
            @Param("type") Integer type,
            @Param("status") Integer status
    );

    // 정산 완료로 변경
    int markPaid(
            @Param("cat_id") int cat_id,
            @Param("tb_id") int tb_id,
            @Param("ref_pk") Long ref_pk
    );

    // 취소 처리 (status = 2)
    int markCancelled(
            @Param("cat_id") int cat_id,
            @Param("tb_id") int tb_id,
            @Param("ref_pk") Long ref_pk
    );
}
