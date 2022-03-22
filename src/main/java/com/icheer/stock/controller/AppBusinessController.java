package com.icheer.stock.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Collections;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.user.entity.WxUser;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;
import com.icheer.stock.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
public class AppBusinessController  extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AppBusinessController.class);





    @Resource
    private UserService userService;

    @Resource
    private WxLoginService wxLoginService;

    @Resource
    private StockInfoService stockInfoService;

    @Resource
    private TradeDataService tradeDataService;


    /**
     * 小程序登录 loginName昵称 userName修改后的名称
     * @param wxLoginInfo
     * @return
     */
    @RequestMapping("/loginByMini")
    @ResponseBody
    public Result loginByMini(@RequestBody WxUser wxLoginInfo) {
        return new Result(200,"","");
    }

    @RequestMapping("/getLoginCode")
    @ResponseBody
    public Result getLoginCode() {
        try {
            return new Result(200,"获取微信登录二维码成功",wxLoginService.getLoginCode());

        } catch (Exception e) {
            throw new BaseException(e.toString());
        }
    }



    /**
     * 获取股票历史数据test
     */
    @RequestMapping("/get000001SZ")
    @ResponseBody
    public Result getcsi300(PageDomain pageDomain)
    {

        pageDomain.setPageNum(1);
        pageDomain.setPageSize(20);

        startPage();
        String code = "000001";

        List<TradeData> list = tradeDataService.listDescByTradeDate(code,100);
        return new Result(200,"",getDataTable(list));
    }

    public static void httpPostForStockList() {
        String url = "http://api.tushare.pro";
        Map<String, Object> params = new HashMap<String, Object>(10);
        params.put("api_name", "stock_basic");
        ////token，需要申请，上面链接直达
        params.put("token", "02168cdd92e4b479dcd581b83d0f6f5114afe93cb4d41b214019022e");
        //请求参数
        Map paramValue=new HashMap();
        paramValue.put("list_status","L");
//        paramValue.put("exchange","SZSE");

        params.put("params", paramValue);
//        params.put("fields", "ts_code,symbol,name,area,industry,fullname,enname,market,exchange,curr_type,list_status,list_date,delist_date,is_hs");
        String res = HttpUtils.sendGet(url, JSON.toJSONString(params));

        System.out.println(res);
        Map result = (Map) JSON.parse(res);
        Map data = (Map) result.get("data");
        JSONArray arr = (JSONArray) data.get("fields");
        JSONArray dataArr = (JSONArray) data.get("items");
        System.out.println(dataArr);
//        for (int i = 0; i < dataArr.size(); i++) {
//            JSONObject obj=dataArr.getJSONObject(i);
//            Object obj = dataArr.get(i);
//
//            System.out.println(obj);
//        }

    }

    /**
     * 搜索股票数据
     * @param stockMap(code) 通过代码搜索个股
     * @param stockMap(name) 通过名称搜索个股
     *
     */
    @RequestMapping("/search_stock")
    @ResponseBody
    public Result search_stockByCode(@RequestBody StockMap stockMap ){

        startPage();
        List<StockTradeResult> stockTradeResults = new ArrayList<>();
        if (stockMap.getCode()!=null & stockMap.getCode()!="")
        {
            StockInfo stock= stockInfoService.getOneByCode(stockMap.getCode());
            /**及其100日数据**/
            if(stock != null)
            {
                List<TradeData> list    = tradeDataService.listDescByTradeDate("000001",100);
                StockTradeResult result = new StockTradeResult();
                result.setStockInfo(stock);
                result.setTradeDataList(list);
                stockTradeResults.add(result);
            }
        }else
        {
            ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
            stockQuery.eq("deleted",0);
            stockQuery.like("name",stockMap.getName());
            List<StockInfo> stocks= stockInfoService.list(stockQuery);
            if (stocks != null)
            {
                for(StockInfo one:stocks)
                {
                    List<TradeData> list    = tradeDataService.listDescByTradeDate("000001",100);
                    StockTradeResult result = new StockTradeResult();
                    result.setStockInfo(one);
                    result.setTradeDataList(list);
                    stockTradeResults.add(result);
                }
            }
        }
        return new Result(200,"success",getDataTable(stockTradeResults));
    }

    /**
     * 搜索股票数据
     * @param stockMap
     * By   名称搜索
     *
     */
    @RequestMapping("/search_stockByName")
    @ResponseBody
    public Result search_stockByName(@RequestBody StockMap stockMap){

        startPage();
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        stockQuery.like("name",stockMap.getName());
        List<StockInfo> stocks= stockInfoService.list(stockQuery);
        return  new Result(200,"success",getDataTable(stocks));

    }

    /**
     * 个股相似走势分析
     * @param  stockMap(code)  比较对象代码
     * @param  stockMap(range) 比较时间范围
     * @return
     */
    @RequestMapping("/stock_analysis")
    @ResponseBody
    public Result stock_analysis(@RequestBody StockMap stockMap)
    {
        /** 对比对象的30交易数据*/
//        List<Double>  cp_open_ls = tradeDataService.getKeyList(stockMap.getCode(),"open",30);
//        List<Double>  cp_close_ls= tradeDataService.getKeyList(stockMap.getCode(),"close",30);
        String key = "ma5";
        List<Double>  cp_ls= tradeDataService.getKeyList(stockMap.getCode(),key,30);
        List <StockInfo> CSI300list=stockInfoService.getCSI300List().subList(0,166);
        Integer total_ranges = 600;
        Integer window_len   = 30;
        List<Double> k_list = new ArrayList<>();
        Map<Double,String>  k_code  =new HashMap<>();
        Map<String,Integer> code_id =new HashMap<>();
        for(StockInfo each : CSI300list)
        {
            Double each_k  = 0.00;
            int   trade_id = 0;
            List<Double> each_trades = tradeDataService.getKeyList(each.getCode(),key,total_ranges);
            List<String>  trades_ids = tradeDataService.listStringByKey(each.getCode(),"id",total_ranges);
            if (each_trades.size()<total_ranges) total_ranges = each_trades.size();

            for(Integer i =0;i<total_ranges;i=i+window_len)
            {
                if(i+stockMap.getRange()>=total_ranges)
                {break;}
                List<Double> each_ls = each_trades.subList(i,stockMap.getRange()+i);
                Double K_like =getPearsonBydim(cp_ls,each_ls);
                if(K_like>each_k){
                    each_k   = K_like;
                    trade_id = Integer.valueOf(trades_ids.get(i));
                }
            }
            k_list.add(each_k);
            k_code.put(each_k,each.getCode());
            code_id.put(each.getCode(),trade_id);
            System.out.println(CSI300list.indexOf(each)+" :"+each.getCode());
        }
        System.out.println(k_list);
        Collections.sort(k_list);
        k_code.get(k_list.get(0));
        System.out.println(k_list);
        return new Result(200,"",k_code);
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
            return null;
        }
    }

}
