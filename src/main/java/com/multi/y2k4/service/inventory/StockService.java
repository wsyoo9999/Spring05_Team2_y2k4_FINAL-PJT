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

    public int manageStock(int stockId, int operationType, Integer quantity) {
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
    @Locked
    public List<Integer> manageStock(List<Integer> stockId , List<Integer> quantity, int operationType) {
        switch (operationType) {
            case 0: // 조회
                if(stockId==null){
                    return null;
                }else{
                    List<Integer> result = new ArrayList<>();
                    for(int i = 0; i < quantity.size(); i++){
                        result.add(getStockQty(stockId.get(i)));
                    }
                    return result;
                }

            case 1: // 증가
                if(stockId.size() != quantity.size()){
                    return null;
                }else{
                    for(int i = 0; i < quantity.size(); i++){
                        updateStockQuantity((stockId.get(i)), quantity.get(i));
                    }
                    return Collections.singletonList(0);
                }

            case 2: // 감소
                if(stockId.size() != quantity.size()){
                    return null;
                }else{
                    for(int i = 0; i < quantity.size(); i++){
                        updateStockQuantity(stockId.get(i), -quantity.get(i));
                    }
                    return Collections.singletonList(0);
                }

            case 3: //체크 후 감소
                if(stockId.size() != quantity.size()){
                    return null;
                }else{
                    for(int i = 0; i < quantity.size(); i++){
                        if(getStockQty(stockId.get(i)) - quantity.get(i) < 0){
                            return null;
                        }
                    }
                    for(int i = 0; i < quantity.size(); i++){
                        updateStockQuantity(stockId.get(i), -quantity.get(i));
                    }
                    return Collections.singletonList(0);
                }

            default:
                throw new IllegalArgumentException("잘못된 호출 형태: " + operationType);
        }
    }

    public int manageStock(int stockId, int operationType) {
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

            System.out.println("재고 수량 변경: stock_id=" + stockId +
                    ", 변경량=" + qtyChange +
                    ", 새 수량=" + newQty);

            return newQty;
        }

        return 0;
    }
}