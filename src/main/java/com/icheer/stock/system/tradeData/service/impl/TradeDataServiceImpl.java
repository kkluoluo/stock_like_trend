package com.icheer.stock.system.tradeData.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.github.pagehelper.PageHelper;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.tradeData.entity.Id_Values;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.mapper.TradeDataMapper;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.userHistory.service.UserHistoryService;
import com.icheer.stock.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TradeDataServiceImpl extends ServiceImpl<TradeDataMapper, TradeData> implements TradeDataService {
    private static final Logger log = LoggerFactory.getLogger(TradeDataServiceImpl.class);

    @Autowired
    private  TradeDataMapper tradeDataMapper;
    @Autowired
    private StockInfoService stockInfoService;

    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private UserHistoryService userHistoryService;

    /**get list By table_name */
    @Override
    public List<TradeData> list(String code){
        return tradeDataMapper.list(tableName_code(code));
    }

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String code, Integer range){

        return tradeDataMapper.listDescByTradeDate(tableName_code(code),range);
    }

    /**倒序 获取字段key 列表 */
    @Override
    public List<Double> getKeyList(String code, String key ,Integer range){

        String  table_name = tableName_code(code);
        return tradeDataMapper.getKeyList(table_name,key,range);
    }

    /**倒序 获取字段key 列表 */
    @Override
    public List<String> listStringByKey(String code, String key ,Integer range){

        String  table_name = tableName_code(code);
        return tradeDataMapper.getStringKeyList(table_name,key,range);
    }

    /**获取getTradeSinceId列表 */
    @Override
    public List<TradeData> getTradeSinceId(String code,  Integer id, Integer range){
        String  table_name = tableName_code(code);
        return tradeDataMapper.getTradeSinceId(table_name,id,range);

    }

    /**获取StockTradeResult*/
    @Override
    public StockTradeResult getStockTradeResult(StockInfo stock){
        List<TradeData> list    = tradeDataMapper.listDescByTradeDate(tableName_code(stock.getCode()),100);
        StockTradeResult result = new StockTradeResult();
        result.setStockInfo(stock);
        result.setTradeDataList(list);
        return  result;
    }

    /**搜索BY code or name */
    @Override
    public IPage<StockTradeResult> searchStockTrades(StockMap stockMap, Long userId){

        List<StockTradeResult> stockTradeResults = new ArrayList<>();
        IPage<StockTradeResult>  tradeResultIPage = new Page<>();
        /**分类搜索**/

//        if (stockMap.getCode()!=null & stockMap.getCode()!="")
//        {
//            userHistoryService.setSearchHistory(Integer.valueOf(userId.toString()),stockMap.getCode());
//            StockInfo stock= stockInfoService.getOneByCode(stockMap.getCode());
//            if(stock != null)
//            {
//                stockTradeResults.add(getStockTradeResult(stock));
//            }
//        }else
//        {
            IPage<StockInfo>  page = new Page<>();
            page.setSize(stockMap.getPageSize());
            page.setCurrent(stockMap.getPageNum());
            ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
            stockQuery.eq("deleted",0);

            if (stockMap.getCode()!=null & stockMap.getCode()!="")
            {
                stockQuery.like("code",stockMap.getCode());
            } else
            {
                stockQuery.like("name",stockMap.getName());
            }


            IPage<StockInfo> stockInfoIPage = stockInfoMapper.selectPage(page,stockQuery);
            tradeResultIPage.setPages(stockInfoIPage.getPages());
            tradeResultIPage.setTotal(stockInfoIPage.getTotal());
            tradeResultIPage.setSize(stockInfoIPage.getSize());
            tradeResultIPage.setCurrent(stockInfoIPage.getCurrent());
            List<StockInfo> stocks= stockInfoIPage.getRecords();
            userHistoryService.setSearchNameHistory(Integer.valueOf(userId.toString()),stockMap.getName());
            if (stocks != null)
            {
                for(StockInfo one:stocks)
                {
                    stockTradeResults.add(getStockTradeResult(one));
                }
            }

//        }
        tradeResultIPage.setRecords(stockTradeResults);
        return tradeResultIPage;
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
            //大盘只显示指数，只返回一条last_one
            List<TradeData> list    = tradeDataMapper.listDescByTradeDate(table_index.get(key),1);
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
    /**获取所以数据正序 */
    @Override
    public List<TradeData> listData(String tableName) {
        return tradeDataMapper.list(tableName);
    }

    @Override
    public Integer getTableSize(String tableName) {
        return tradeDataMapper.getTableSize(tableName);
    }

    /**获取相似分析 ----new----*/
    @Override
    public  List<StockSimilar> getSimilar_Analysis(String code , int range,int pre_range, String key){
        String  cp_table = tableName_code(code);
        String  cp_name  = stockInfoService.getOneByCode(code).getName();
        List<Double>  cp_ls= tradeDataMapper.getKeyList(cp_table,key,range);
        List <StockInfo> CSI300list= stockInfoMapper.getCsi300_ts_code_name();
        Integer total_ranges = 600;
        Integer window_len   = 30;
        List<Double> k_list = new ArrayList<>();
        Map<Double,StockInfo> k_stock =new HashMap<>();
//        Map<String,Integer> code_id =new HashMap<>();
        for(StockInfo each : CSI300list)
        {
            Double each_k  = 0.00;
//            int   trade_id = 0;
            String each_table = each.getTsCode().replace(".","_").toLowerCase();
            List<Double> each_trades = tradeDataMapper.getKeyList(each_table,key,total_ranges);
            List<String> each_index =  tradeDataMapper.getStringKeyList(each_table,"id",total_ranges);
//            List<Double> each_trades = new ArrayList<>();
//            List<Id_Values> idValues = tradeDataMapper.getIdAndKeyList(each_table,key,total_ranges);
//            for (Id_Values idValues1 : idValues)
//            {each_trades.add(idValues1.getMa5());}
            if (each_trades.size()<total_ranges) total_ranges = each_trades.size();
            for(Integer i =pre_range;i<total_ranges;i=i+window_len)
            {
                if(i+range>=total_ranges)
                {break;}
                Double K_like =getPearsonBydim(cp_ls,each_trades.subList(i,range+i));
                if(K_like>each_k){
                    each_k   = K_like;
                    /**---交易日期---**/
//                    trade_id = idValues.get(i).getId();
//                    each.setListDate(idValues.get(i).getTradeDate());
//                    each.setId(idValues.get(i).getId());
                    each.setId(Integer.valueOf(each_index.get(i)));
                }
            }
            k_list.add(each_k);
            k_stock.put(each_k,each);
//            code_id.put(each.getCode(),trade_id);
        }
        //排序
        Collections.sort(k_list,Collections.reverseOrder());
        //返回数据整合
        List<StockSimilar> similarList = new ArrayList<>();
        for( double similar:k_list.subList(0,10))
        {
            StockSimilar stockSimilar = new StockSimilar();
            stockSimilar.setSimilar(similar);
            StockInfo stock_info = k_stock.get(similar);
            stockSimilar.setCode(k_stock.get(similar).getCode());
            stockSimilar.setName(k_stock.get(similar).getName());
            String si_table = stock_info.getTsCode().replace(".","_").toLowerCase();
            TradeData last_trade = tradeDataMapper.getById(si_table,stock_info.getId());
            stockSimilar.setLastDate(last_trade.getTradeDate());
            stockSimilar.setStartDate(tradeDataMapper.getById(si_table,stock_info.getId()-range).getTradeDate());
            /**--预测涨跌幅---**/
            double pre_pct = (tradeDataMapper.getById(si_table,stock_info.getId()+pre_range).getClose()-last_trade.getClose())/last_trade.getClose();
            stockSimilar.setChange(pre_pct);
            //            Integer indexId = code_id.get(k_stock.get(similar).getCode()) -range;
//            String si_table = k_stock.get(similar).getTsCode().replace(".","_").toLowerCase();
            stockSimilar.setTradeData(tradeDataMapper.getTradeSinceId(si_table,stock_info.getId()-range,range+pre_range));
            similarList.add(stockSimilar);
        }
        StockSimilar similar_cp = new StockSimilar();
        similar_cp.setSimilar(1.0);
        similar_cp.setCode(code);
        similar_cp.setName(cp_name);
        Integer cp_last_id= tradeDataMapper.listDescByTradeDate(cp_table,1).get(0).getId();
        List<TradeData> cpTradeDataList = tradeDataMapper.getTradeSinceId(cp_table,cp_last_id-range,range);
        similar_cp.setLastDate(tradeDataMapper.listDescByTradeDate(cp_table,1).get(0).getTradeDate());
        similar_cp.setStartDate(cpTradeDataList.get(0).getTradeDate());
        /**----空数据添加----*/
        for (int day=1;day<=pre_range ;day++)
        {
          TradeData  preTradeData = tradeDataMapper.listDescByTradeDate(cp_table,1).get(0);
          preTradeData.setTradeDate(tradeDataMapper.listDescByTradeDate(cp_table,1).get(0).getTradeDate().plusDays(day));
          cpTradeDataList.add(preTradeData);
        }
        similar_cp.setTradeData(cpTradeDataList);

        similarList.add(0,similar_cp);
        System.out.println(k_list);
        return similarList;
    }


    /**获取相似分析 */
    @Override
    public  List<StockSimilar> getSimilarAnalysis(String code , int range,int pre_range, String key){
        String  cp_table = tableName_code(code);
        List<Double>  cp_ls= tradeDataMapper.getKeyList(cp_table,key,range);
        List <StockInfo> CSI300list= stockInfoMapper.getCsi300_ts_code_name();
        Integer total_ranges = 600;
        Integer window_len   = range/6;
        List<Double> k_list = new ArrayList<>();

        Map<Double,StockInfo> k_stock =new HashMap<>();
        Map<String,Integer> code_id =new HashMap<>();
        for(StockInfo each : CSI300list)
        {
            Double each_k  = 0.00;
            int   trade_id = 0;
            String each_table = each.getTsCode().replace(".","_").toLowerCase();
              List<Double> each_trades = new ArrayList<>();
              List<Id_Values> idValues = tradeDataMapper.getIdAndKeyList(each_table,key,total_ranges);
              for (Id_Values idValues1 : idValues)
              {each_trades.add(idValues1.getMa5());}
            if (each_trades.size()<total_ranges) total_ranges = each_trades.size();
            for(Integer i =pre_range;i<total_ranges;i=i+window_len)
            {
                if(i+range>=total_ranges)
                {break;}
                List<Double> each_ls = each_trades.subList(i,range+i);
                Double K_like =getPearsonBydim(cp_ls,each_ls);
                if(K_like>each_k){
                    each_k   = K_like;
                    trade_id = idValues.get(i).getId();
                }
            }
            k_list.add(each_k);
            k_stock.put(each_k,each);
            code_id.put(each.getCode(),trade_id);
        }
        Collections.sort(k_list,Collections.reverseOrder());
        System.out.println(k_list);
        List<StockSimilar> similarList = new ArrayList<>();
        for( double similar:k_list.subList(0,10))
        {
            StockSimilar stockSimilar = new StockSimilar();
            stockSimilar.setSimilar(similar);
//            StockInfo stock_info = k_stock.get(similar);
            stockSimilar.setCode(k_stock.get(similar).getCode());
            stockSimilar.setName(k_stock.get(similar).getName());
            Integer indexId = code_id.get(k_stock.get(similar).getCode()) -range;
            String si_table = k_stock.get(similar).getTsCode().replace(".","_").toLowerCase();
            stockSimilar.setTradeData(tradeDataMapper.getTradeSinceId(si_table,indexId,range+pre_range));
            similarList.add(stockSimilar);
        }

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


    public String tableName_code(String code)
    {
        String  table_name = stockInfoMapper.getTsByCode(code).replace(".","_").toLowerCase();
        return table_name;
    }

    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

}



