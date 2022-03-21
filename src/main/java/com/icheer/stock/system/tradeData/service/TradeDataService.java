package com.icheer.stock.system.tradeData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.tradeData.entity.TradeData;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

public interface TradeDataService  extends IService<TradeData> {

    /**get list By table_name */
    public List<TradeData> list(String table_name);

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String table_name, Integer range);


}
