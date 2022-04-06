package com.icheer.stock.controller.test;

import com.icheer.stock.controller.test.test.entity.Test;
import com.icheer.stock.controller.test.test.service.TestService;
import com.icheer.stock.system.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;
import com.icheer.stock.util.Result;
import com.icheer.stock.util.ServerVersion;
import com.icheer.stock.util.StockMap;
import com.icheer.stock.util.StockTradeResult;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/test")
public class indexController {


    @Resource
    private UserService userService;

    @Resource
    private WxLoginService wxLoginService;

    @Resource
    private ServerVersion serverVersion;

    @Resource
    private StockInfoService stockInfoService;

    @Resource
    private TradeDataService tradeDataService;

    @Resource
    private TestService testService;

    @Resource
    private StockInfoMapper stockInfoMapper;


    /**
     * 个股相似走势分析
     * @param  stockMap(code)  比较对象代码
     * @param  stockMap(range) 比较时间范围
     * @return
     */
    @RequestMapping("/stock_new")
    @ResponseBody
    public Result stock_analysis2(@RequestBody StockMap stockMap) throws IOException
    {
      List<StockSimilar>  list=  tradeDataService.getSimilarAnalysis(stockMap.getCode(),stockMap.getRange(),"ma5");
      return  new Result(200,"",list);
    }


    @RequestMapping("/stock_test")
    @ResponseBody
    public Result stock_test(@RequestBody StockMap stockMap) throws IOException
    {

        /**分类搜索**/
        List<StockTradeResult> stockTradeResults = tradeDataService.searchStockTrades(stockMap,Long.valueOf(1));
        return new Result(200,"success",stockTradeResults);

    }




    /**
     * 个股相似走势分析
     * @param  stockMap(code)  比较对象代码
     * @param  stockMap(range) 比较时间范围
     * @return
     */
    @RequestMapping("/stock_analysis")
    @ResponseBody
    public Result stock_analysis(@RequestBody StockMap stockMap) throws IOException
    {


        /** 对比对象的30交易数据*/
        String key = "ma5";
        List<Double> cp_ls= tradeDataService.getKeyList(stockMap.getCode(),key,stockMap.getRange());
        List <StockInfo> CSI300list=stockInfoService.getCSI300List();
        Integer total_ranges = 600;
        Integer window_len   = 30;
        List<Double> k_list = new ArrayList<>();
        Map<Double,String> k_code  =new HashMap<>();
        Map<String,Integer> code_id =new HashMap<>();
        for(StockInfo each : CSI300list)
        {
            Double each_k  = 0.00;
            int   trade_id = 0;
            List<Double> each_trades = tradeDataService.getKeyList(each.getCode(),key,total_ranges);
            List<String>  trades_ids = tradeDataService.listStringByKey(each.getCode(),"id",total_ranges);
            if (each_trades.size()<total_ranges) total_ranges = each_trades.size();

            for(Integer i =10;i<total_ranges;i=i+window_len)
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
        }
        Collections.sort(k_list,Collections.reverseOrder());
        List<List> similarList = new ArrayList<>();
        for( double similar:k_list.subList(0,10))
        {
            StockSimilar stockSimilar = new StockSimilar();
//            List<String> list  = new ArrayList<>();
//            String code = k_code.get(similar);
//            list.add(code);
//            list.add(String.valueOf(similar));
//            list.add(stockInfoService.getOneByCode(code).getName());
//            list.add(code_id.get(code).toString());
            Test t = new Test();
            t.setK(similar);
            String code = k_code.get(similar);
            t.setCode(stockInfoService.getOneByCode(code).getTsCode().toLowerCase(Locale.ROOT));
            t.setName(stockInfoService.getOneByCode(code).getName());
            t.setDateId(code_id.get(code).toString());
            t.setAnCode(stockMap.getCode());
            t.setMo(String.valueOf(stockMap.getRange()));
            testService.save(t);
        }
        System.out.println(k_list);

        return new Result(200,"success",similarList);
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
            return 0.0;
        }
    }
}