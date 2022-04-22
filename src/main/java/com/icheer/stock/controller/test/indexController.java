package com.icheer.stock.controller.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import com.icheer.stock.controller.test.testResult.entity.Test;
import com.icheer.stock.controller.test.testResult.service.TestService;
import com.icheer.stock.framework.aop.NoRepeatSubmit;
import com.icheer.stock.system.processedTabel.service.ProcessedTableService;
import com.icheer.stock.system.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.tradeData.entity.Id_Values;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.mapper.TradeDataMapper;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;
import com.icheer.stock.util.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/test")
public class indexController extends  BaseController{

    @Resource
    private StockInfoService stockInfoService;

    @Resource
    private TradeDataService tradeDataService;

    @Resource
    private TestService testService;

    @Resource
    private StockInfoMapper stockInfoMapper;
    @Resource
    private TradeDataMapper tradeDataMapper;

    @Resource
    private ProcessedTableService processedTableService;

    /**
     * K线相似
     * @param stockMap /
     * @return /
     */
    @RequestMapping("/getSimilarRes_TEST")
    public Result getSimilarRes(@RequestBody StockMap stockMap) {
        List<StockSimilar> similarList = processedTableService.getKLineSimilar(stockMap);
        ArrayList<Test> testRes = new ArrayList<>();
        for( StockSimilar similar_kL : similarList) {
            Test t = new Test();
            t.setK(similar_kL.getSimilar());
            t.setCode(stockInfoService.getOneByCode(similar_kL.getCode()).getTsCode().toLowerCase());
            t.setName(stockInfoService.getOneByCode(similar_kL.getCode()).getName());
            t.setDateId(String.valueOf(similar_kL.getTradeData().get(0).getId()));
            t.setAnCode(stockMap.getCode());
            t.setMo(String.valueOf(stockMap.getRange()));
            t.setFlag("h");
            testRes.add(t);
        }
        testService.saveBatch(testRes);
        return new Result(200, "", similarList);
    }

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
      List<StockSimilar>  list=  tradeDataService.getSimilarAnalysis(stockMap.getCode(),stockMap.getRange(),stockMap.getPreRange(),"ma5");
      return  new Result(200,"",list);
    }

    /**
     * 搜索股票数据
     * @param stockMap(code) 通过代码搜索个股
     * @param stockMap(name) 通过名称搜索个股
     *
     */
    @RequestMapping("/search_stock")
    @ResponseBody
    public Result search_stockByCode(@RequestBody StockMap stockMap  ,PageDomain pageDomain){
        /**userLogger**/


        /**分类搜索**/
        IPage<StockTradeResult> stockTradeResults = tradeDataService.searchStockTrades(stockMap,Long.valueOf(1));
        return new Result(200,"success",stockTradeResults);
    }

    /**
     * 个股相似走势分析----威尔逊相干法
     * @param  stockMap(code)  比较对象代码
     * @param  stockMap(range) 比较时间范围
     * @return
     */
    @RequestMapping("/stock_analysis")
    @ResponseBody
    @NoRepeatSubmit
    public Result stock_analysis(@RequestBody StockMap stockMap) throws IOException
    {

        /** 对比对象的30交易数据*/
        int preRange = 5;
        if (stockMap.getRange()>preRange) preRange =stockMap.getPreRange();
        System.out.println(stockMap);
        if (stockMap.getModel().equalsIgnoreCase("KL"))
        {
            List<StockSimilar> similarList = processedTableService.getKLineSimilar(stockMap);
            return new Result(200, "", similarList);
        }else
        {
            List<StockSimilar>  similarList= tradeDataService.getSimilar_Analysis(stockMap.getCode(),stockMap.getRange(),preRange,"ma5");
            return new Result(200,"",similarList);
        }

    }


    /**
     * 市场行情-大盘综合指数
     *
     * @return  近100日行情数据
     */
    @RequestMapping("/composite_index")
    @ResponseBody
    public Result composite_index0() throws IOException {

        return  new Result(200,"",tradeDataService.getCompositeIndex());
    }

    /**
     * 市场行情-大盘综合指数
     *
     * @return  分时数据，调用python脚本获取更新mysql数据再获取（存在延时）
     */
    @RequestMapping("/composite_index_min")
    @ResponseBody
    public Result composite_index() throws IOException {
        return  new Result(200,"" ,testPython());
    }
        boolean testPython () {
            String command = "cmd.exe /c cd  D:\\project\\stock_backend " + "&& start python test.py ";
            try {
                Process p = Runtime.getRuntime().exec(command);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

    /**
     * 个股相似走势分析
     * @param  stockMap(code)  比较对象代码
     * @param  stockMap(range) 比较时间范围
     * @return
     */
    @RequestMapping("/stock_saveTest")
    @ResponseBody
    public Result stock_analysis_test(@RequestBody StockMap stockMap) throws IOException
    {



//        List<List> similarList = new ArrayList<>();
//        for( double similar:k_list.subList(0,10))
//        {
//            StockSimilar stockSimilar = new StockSimilar();
//            Test t = new Test();
//            t.setK(similar);
//            String code = k_code.get(similar);
//            t.setCode(stockInfoService.getOneByCode(code).getTsCode().toLowerCase(Locale.ROOT));
//            t.setName(stockInfoService.getOneByCode(code).getName());
//            t.setDateId(code_id.get(code).toString());
//            t.setAnCode(stockMap.getCode());
//            t.setMo(String.valueOf(stockMap.getRange()));
//            testService.save(t);
//        }
//        System.out.println(k_list);

        return new Result(200,"success","success");
    }


}
