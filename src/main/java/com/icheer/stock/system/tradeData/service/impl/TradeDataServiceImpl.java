package com.icheer.stock.system.tradeData.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.mapper.TradeDataMapper;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.userHistory.service.UserHistoryService;
import com.icheer.stock.util.ExcludeEmptyQueryWrapper;
import com.icheer.stock.util.StockMap;
import com.icheer.stock.util.StockTradeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TradeDataServiceImpl extends ServiceImpl<TradeDataMapper, TradeData> implements TradeDataService {
    private static final Logger log = LoggerFactory.getLogger(TradeDataServiceImpl.class);

    @Autowired
    private  TradeDataMapper tradeDataMapper;
    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private UserHistoryService userHistoryService;

    /**get list By table_name */
    public List<TradeData> list(String code){
        return tradeDataMapper.list(tableName_code(code));
    }

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String code, Integer range){

        return tradeDataMapper.listDescByTradeDate(tableName_code(code),range);
    }

    /**倒序 获取字段key 列表 */
    public List<Double> getKeyList(String code, String key ,Integer range){

        String  table_name = tableName_code(code);
        return tradeDataMapper.getKeyList(table_name,key,range);
    }

    /**倒序 获取字段key 列表 */
    public List<String> listStringByKey(String code, String key ,Integer range){

        String  table_name = tableName_code(code);
        return tradeDataMapper.getStringKeyList(table_name,key,range);
    }

    /**获取getTradeSinceId列表 */
    public List<TradeData> getTradeSinceId(String code,  Integer id, Integer range){
        String  table_name = tableName_code(code);
        return tradeDataMapper.getTradeSinceId(table_name,id,range);

    }

    /**获取StockTradeResult*/
    public StockTradeResult getStockTradeResult(StockInfo stock){
        List<TradeData> list    = tradeDataMapper.listDescByTradeDate(tableName_code(stock.getCode()),100);
        StockTradeResult result = new StockTradeResult();
        result.setStockInfo(stock);
        result.setTradeDataList(list);
        return  result;
    }

    /**搜索BY code or name */
    public List<StockTradeResult> searchStockTrades(StockMap stockMap, Long userId){

        List<StockTradeResult> stockTradeResults = new ArrayList<>();
        /**分类搜索**/
        if (stockMap.getCode()!=null & stockMap.getCode()!="")
        {
            userHistoryService.setSearchHistory(Integer.valueOf(userId.toString()),stockMap.getCode());
            StockInfo stock= stockInfoMapper.getByCode(stockMap.getCode());
            if(stock != null)
            {
                stockTradeResults.add(getStockTradeResult(stock));
            }
        }else
        {
//            ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
//            stockQuery.eq("deleted",0);
//            stockQuery.like("name",stockMap.getName());
//            List<StockInfo> stocks= stockInfoMapper.selectList(stockQuery);
            List<StockInfo> stocks= stockInfoMapper.listByName(stockMap.getName());
            userHistoryService.setSearchNameHistory(Integer.valueOf(userId.toString()),stockMap.getName());
            if (stocks != null)
            {
                for(StockInfo one:stocks)
                {
                    stockTradeResults.add(getStockTradeResult(one));
                }
            }
        }
        return stockTradeResults;
    }


    /** 大盘交易数据 */
    public List<StockTradeResult> getCompositeIndex(){

        List<StockTradeResult> stockTradeResults = new ArrayList<>();
        Map<String,String> table_index = new HashMap<>();
        table_index.put("上证指数","sh_000001");
        table_index.put("深证指数","sz_399001");
        table_index.put("创业板指数","sz_399006");

        for (String key:table_index.keySet())
        {
            List<TradeData> list    = tradeDataMapper.listDescByTradeDate(table_index.get(key),100);
            StockTradeResult result = new StockTradeResult();
            StockInfo composite = new StockInfo();
            composite.setName(key);
            composite.setTsCode(list.get(0).getTsCode());
            result.setStockInfo(composite);
            result.setTradeDataList(list);
            stockTradeResults.add(result);
        }
        return  stockTradeResults;
    }




    /**获取相似分析 */
    public  List<StockSimilar> getSimilarAnalysis(String code , int range, String key){
        String  cp_table = tableName_code(code);
        List<Double>  cp_ls= tradeDataMapper.getKeyList(cp_table,key,range);
        List <StockInfo> CSI300list= stockInfoMapper.getCsi300();
        Integer total_ranges = 600;
        Integer window_len   = 30;
        List<Double> k_list = new ArrayList<>();
        Map<Double,String> k_code  =new HashMap<>();
        Map<String,Integer> code_id =new HashMap<>();
        for(StockInfo each : CSI300list)
        {
            Double each_k  = 0.00;
            int   trade_id = 0;
            String each_table = tableName_code(each.getCode());
            List<Double> each_trades = tradeDataMapper.getKeyList(each_table,key,total_ranges);
            List<String>  trades_ids = tradeDataMapper.getStringKeyList(each_table,"id",total_ranges);
            if (each_trades.size()<total_ranges) total_ranges = each_trades.size();
            for(Integer i =10;i<total_ranges;i=i+window_len)
            {
                if(i+range>=total_ranges)
                {break;}
                List<Double> each_ls = each_trades.subList(i,range+i);
                Double K_like =getPearsonBydim(cp_ls,each_ls);
                if(K_like>each_k){
                    each_k   = K_like;
                    trade_id = Integer.valueOf(trades_ids.get(i));
                }
            }
            k_list.add(each_k);
            k_code.put(each_k,each.getCode());
            code_id.put(each.getCode(),trade_id);
        }
        Collections.sort(k_list,Collections.reverseOrder());
        List<StockSimilar> similarList = new ArrayList<>();
        for( double similar:k_list.subList(0,10))
        {
            StockSimilar stockSimilar = new StockSimilar();
            stockSimilar.setSimilar(similar);
            String si_code = k_code.get(similar);
            stockSimilar.setCode(si_code);
            stockSimilar.setName(stockInfoMapper.getByCode(si_code).getName());
            Integer indexId = code_id.get(si_code) -range;
            stockSimilar.setTradeData(tradeDataMapper.getTradeSinceId(tableName_code(si_code),indexId,range+20));
            similarList.add(stockSimilar);
        }
        System.out.println(k_list);
    return similarList;
    }

    /**
     * 皮尔逊相关度系数计算
     * @param ratingOne
     * @param ratingTwo
     * @return
     */
    public Double getPearsonBydim(List<Double> ratingOne, List<Double> ratingTwo) {
        try {
            if(ratingOne.size() != ratingTwo.size()) {//两个变量的观测值是成对的，每对观测值之间相互独立。
                if(ratingOne.size() > ratingTwo.size()) {//保留小的处理大
                    List<Double> temp = ratingOne;
                    ratingOne = new ArrayList<>();
                    for(int i=0;i<ratingTwo.size();i++) {
                        ratingOne.add(temp.get(i));
                    }
                }else {
                    List<Double> temp = ratingTwo;
                    ratingTwo = new ArrayList<>();
                    for(int i=0;i<ratingOne.size();i++) {
                        ratingTwo.add(temp.get(i));
                    }
                }
            }
            double sim = 0D;//最后的皮尔逊相关度系数
            double commonItemsLen = ratingOne.size();//操作数的个数
            double oneSum = 0D;//第一个相关数的和
            double twoSum = 0D;//第二个相关数的和
            double oneSqSum = 0D;//第一个相关数的平方和
            double twoSqSum = 0D;//第二个相关数的平方和
            double oneTwoSum = 0D;//两个相关数的乘积和
            for(int i=0;i<ratingOne.size();i++) {//计算
                double oneTemp = ratingOne.get(i);
                double twoTemp = ratingTwo.get(i);
                //求和
                oneSum += oneTemp;
                twoSum += twoTemp;
                oneSqSum += Math.pow(oneTemp, 2);
                twoSqSum += Math.pow(twoTemp, 2);
                oneTwoSum += oneTemp*twoTemp;
            }
            double num = (commonItemsLen*oneTwoSum) - (oneSum*twoSum);
            double den = Math.sqrt((commonItemsLen * oneSqSum - Math.pow(oneSum, 2)) * (commonItemsLen * twoSqSum - Math.pow(twoSum, 2)));
            sim = (den == 0) ? 1 : num / den;
            return sim;
        } catch (Exception e) {
            return 0.00;
        }
    }

    /** 皮尔逊相关度系数 int计算，但是速度没有优化太多and结果会被0.0*/
    /*
    public static Double getPearsonBydim2(List<Double> ratingOne, List<Double> ratingTwo) {
        if(ratingOne.size() != ratingTwo.size()) {//两个变量的观测值是成对的，每对观测值之间相互独立。
            return null;
        }
        double sim = 0D;//最后的皮尔逊相关度系数
        int commonItemsLen = ratingOne.size();//操作数的个数
        int oneSum = 0;//第一个相关数的和
        int twoSum = 0;//第二个相关数的和
        for(int i=0; i<commonItemsLen; i++) {
            oneSum += ratingOne.get(i)*100;
            twoSum += ratingTwo.get(i)*100;
        }
        int oneAvg = oneSum/commonItemsLen;//第一个相关数的平均值
        int twoAvg = twoSum/commonItemsLen;//第二个相关数的平均值
        int sonSum = 0;
        int tempOne = 0;
        int tempTwo = 0;
        for(int i=0; i<commonItemsLen; i++) {
            sonSum += (ratingOne.get(i)*100-oneAvg)*(ratingTwo.get(i)-twoAvg);
            tempOne += Math.pow((ratingOne.get(i)*100-oneAvg), 2);
            tempTwo += Math.pow((ratingTwo.get(i)*100-twoAvg), 2);
        }
        double fatherSum = Math.sqrt(tempOne * tempTwo);
        sim = (fatherSum == 0) ? 1 : sonSum / fatherSum;
        return sim;
    }

     */

    String tableName_code(String code)
    {
        String  table_name = stockInfoMapper.getTsByCode(code).replace(".","_").toLowerCase();
        return table_name;
    }


}



