package com.icheer.stock.util;

import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.tradeData.entity.TradeData;
import lombok.Data;

import java.util.List;

@Data
public class StockTradeResult {
    private StockInfo stockInfo;
    private List<TradeData> tradeDataList;
}
