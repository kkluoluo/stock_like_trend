package com.icheer.stock.controller;

import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.system.user.entity.WxUser;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;


import com.icheer.stock.system.userHistory.service.UserHistoryService;
import com.icheer.stock.util.*;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/api")
public class AppBusinessController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(AppBusinessController.class);



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
    private UserHistoryService userHistoryService;




    /**
     * version
     */
    @RequestMapping("/version")
    @ResponseBody
    public Result getVersion(){
        Map<String ,String> result = new HashMap<>();
        result.put("stock-backend",serverVersion.getVersion());
        result.put("Time",LocalDateTime.now().toString());
        return new Result(200,"SUCCESS",result);
    }

    /**
     * 小程序登录 loginName昵称 userName修改后的名称
     * @param wxLoginInfo
     * @return
     */
    @RequestMapping("/loginByMini")
    @ResponseBody
    public Result loginByMini(@RequestBody WxUser wxLoginInfo) {
        boolean result = false;
        WxUser wxLoginUserToken = new WxUser();
        try{
            wxLoginUserToken =  wxLoginService.wxUserLogin(wxLoginInfo);
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(result == true) {
                wxLoginUserToken.setAvatarUrl(wxLoginUserToken.getAvatar());
                wxLoginUserToken.setNickName(wxLoginUserToken.getLoginName());
                return new Result(200,"登录成功",wxLoginUserToken);
            } else {
                return  new Result(200,"登录失败","");
            }
        }
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
     * 搜索股票数据
     * @param stockMap(code) 通过代码搜索个股
     * @param stockMap(name) 通过名称搜索个股
     *
     */
    @RequestMapping("/search_stock")
    @ResponseBody
    public Result search_stockByCode(@RequestBody StockMap stockMap ){
        /**userLogger**/
        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");
        /**分类搜索**/
        List<StockTradeResult> stockTradeResults = tradeDataService.searchStockTrades(stockMap,userId);
        return new Result(200,"success",getDataTable(stockTradeResults));
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
        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");
        userHistoryService.setSearchHistory(Integer.valueOf(userId.toString()),stockMap.getCode());

        /** 对比对象的30交易数据*/
        int preRange = 5;
        if (stockMap.getRange()>preRange) preRange =stockMap.getPreRange();
        List<StockSimilar>  similarList=  tradeDataService.getSimilarAnalysis(stockMap.getCode(),stockMap.getRange(),preRange,"ma5");
        return new Result(200,"",similarList);
    }


    @RequestMapping("/stock_test")
    @ResponseBody
    public Result stock_test(@RequestBody StockMap stockMap) throws IOException
    {
        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");
        userHistoryService.setSearchHistory(Integer.valueOf(userId.toString()),stockMap.getCode());

        /** 对比对象的30交易数据*/
        int preRange = 5;
        if (stockMap.getRange()>preRange) preRange =stockMap.getPreRange();
        List<StockSimilar>  list=  tradeDataService.getSimilar_test(stockMap.getCode(),stockMap.getRange(),preRange,"ma5");
        return  new Result(200,"",list);

    }

    /**
     * 市场行情-大盘综合指数
     * @return
     */
    @RequestMapping("/composite_index")
    @ResponseBody
    public Result composite_index() throws IOException {

        return  new Result(200,"",getDataTable(tradeDataService.getCompositeIndex()));
    }



}
