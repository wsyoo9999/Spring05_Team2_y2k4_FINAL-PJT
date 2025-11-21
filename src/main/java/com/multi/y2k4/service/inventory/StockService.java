package com.multi.y2k4.service.inventory;

import com.multi.y2k4.mapper.tenant.inventory.StockMapper;
import com.multi.y2k4.vo.inventory.Stock;
import lombok.Locked;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockMapper stockMapper;

    public List<Stock> list_all() {
        return stockMapper.list_all();
    }

    public int addStock(Stock stock) {
        return stockMapper.addStock(stock);
    }

    public Stock selectStockById(Integer stock_id) { return stockMapper.selectStockById(stock_id);  }
    public int updateStock(Stock stock) {
        return stockMapper.updateStock(stock);
    }

    public int deleteStock(Stock stock) {return stockMapper.deleteStock(stock);}

    public List<Stock> list(Stock stock) {
        return stockMapper.list(stock);
    }

    @Locked
    public int manageAcquiredAty(int stockId, int operationType, Integer quantity) {
        switch (operationType) {
            case 0: // 조회
                return getAcquiredAty(stockId);

            case 1: // 증가
                return updateAcquiredAty(stockId, quantity);

            case 2: // 감소
                return updateAcquiredAty(stockId, -quantity);

            default:
                throw new IllegalArgumentException("잘못된 호출 형태: " + operationType);
        }
    }
    @Locked
    public List<Integer> manageStock(List<Integer> stockId , List<Integer> real_qty, List<Integer> acquired_qty, int operationType) {
        switch (operationType) {
            case 0: // 조회
                if(stockId==null){
                    return null;
                }else{
                    List<Integer> result = new ArrayList<>();
                    for (Integer integer : stockId) {
                        result.add(getStockQty(integer));
                    }
                    return result;
                }

            case 1: // 증가
                if(stockId ==null || real_qty == null || stockId.size() != real_qty.size()){
                    return null;
                }else{
                    for(int i = 0; i < stockId.size(); i++){
                        updateStockQuantity((stockId.get(i)), real_qty.get(i));
                    }
                    return Collections.singletonList(0);
                }

            case 2: //체크 후 감소
                if(stockId ==null || real_qty == null || acquired_qty == null || stockId.size() != real_qty.size()|| real_qty.size() != acquired_qty.size()) {
                    return null;
                }else{
                    for(int i = 0; i < real_qty.size(); i++){
                        if(getStockQty(stockId.get(i)) - real_qty.get(i) < 0){
                            return null;
                        }
                    }
                    for(int i = 0; i < real_qty.size(); i++){
                        updateStockQuantity(stockId.get(i), -real_qty.get(i));
                        updateAcquiredAty(stockId.get(i), -acquired_qty.get(i));
                    }
                    return Collections.singletonList(0);
                }

            default:
                throw new IllegalArgumentException("잘못된 호출 형태: " + operationType);
        }
    }

    public int manageAcquiredAty(int stockId, int operationType) {
        if (operationType != 0) {
            throw new IllegalArgumentException("증감 작업에는 수량이 필요합니다.");
        }
        return getAcquiredAty(stockId);
    }

    public int getAcquiredAty(int stockId) {
        Stock stock = stockMapper.selectStockById(stockId);
        return stock != null ? stock.getAcquired_qty() : 0;
    }


    private int updateAcquiredAty(int stockId, int qtyChange) {
        Stock stock = stockMapper.selectStockById(stockId);

        if (stock != null) {
            int newQty = stock.getAcquired_qty() + qtyChange;

            stock.setAcquired_qty(newQty);
            stockMapper.updateStock(stock);

            return newQty;
        }

        return 0;
    }

    public int manageStockQty(int stockId, int operationType, Integer quantity) {
        switch (operationType) {
            case 0: // 조회
                return getStockQty(stockId);

            case 1: // 증가
                return updateStockQuantity(stockId, quantity);

            case 2: // 감소
                return updateStockQuantity(stockId, -quantity);

            default:
                throw new IllegalArgumentException("잘못된 호출 형태: " + operationType);
        }
    }


    public int manageStockQty(int stockId, int operationType) {
        if (operationType != 0) {
            throw new IllegalArgumentException("증감 작업에는 수량이 필요합니다.");
        }
        return getStockQty(stockId);
    }


    public int getStockQty(int stockId) {
        Stock stock = stockMapper.selectStockById(stockId);
        return stock != null ? stock.getQty() : 0;
    }


    private int updateStockQuantity(int stockId, int qtyChange) {
        Stock stock = stockMapper.selectStockById(stockId);

        if (stock != null) {
            int newQty = stock.getQty() + qtyChange;

            stock.setQty(newQty);
            stockMapper.updateStock(stock);

            return newQty;
        }

        return 0;
    }
}