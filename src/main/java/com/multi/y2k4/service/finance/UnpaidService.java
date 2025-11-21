package com.multi.y2k4.service.finance;

import com.multi.y2k4.mapper.tenant.finance.UnpaidMapper;
import com.multi.y2k4.vo.finance.Unpaid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnpaidService {

    private final UnpaidMapper unpaidMapper;


    public void upsertUnpaid(Unpaid unpaid) {

        unpaidMapper.upsertUnpaid(unpaid);
    }

    public void cancelUnpaid(int cat_id, int tb_id, Long ref_pk) {
        unpaidMapper.markCancelled(cat_id,tb_id,ref_pk); // cat=1, tb=0(판매)
    }


    public void markPaid(int cat_id, int tb_id, Long ref_pk) {
        unpaidMapper.markPaid(cat_id,tb_id,ref_pk);
    }

    public Unpaid searchByBusiness(int cat_id,int tb_id,Long ref_pk) {
        return unpaidMapper.searchByBusiness(cat_id,tb_id,ref_pk);
    }

    public Unpaid searchById(long unpaid_id) {
        return  unpaidMapper.searchById(unpaid_id);
    }

    public List<Unpaid> list( Integer cat_id,Integer tb_id,Integer type,Integer status) {
        return unpaidMapper.list(cat_id,tb_id,type,status);
    }


}
