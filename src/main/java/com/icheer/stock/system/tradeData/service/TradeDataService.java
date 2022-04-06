package com.icheer.stock.system.tradeData.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.util.StockMap;
import com.icheer.stock.util.StockTradeResult;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeDataService  extends IService<TradeData> {

    /**get list By table_name */
    public List<TradeData> list(String code);

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String code, Integer range);

    /**倒序 获取字段key 列表 */
    public List<Double> getKeyList(String code, String key ,Integer range);

    /**倒序 获取字段key 列表 */
    public List<String> listStringByKey(String code, String key ,Integer range);

    /**获取getTradeSinceId列表 */
    public List<TradeData> getTradeSinceId(String code,  Integer id, Integer range);

    /**获取相似分析 */
    public  List<StockSimilar> getSimilarAnalysis(String code , int range, String key);

    /**获取StockTradeResult*/
    public StockTradeResult getStockTradeResult(StockInfo stock);

    /**搜索BY code or name */
    public List<StockTradeResult> searchStockTrades(StockMap stockMap , Long userId);


}
