package com.icheer.stock.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icheer.stock.system.CSI300.mapper.CSI300StockMapper;
import com.icheer.stock.system.CSI300.service.CSI300StockService;
import com.icheer.stock.system.stockData.entity.StockData;
import com.icheer.stock.system.stockData.service.StockDataService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.user.entity.WxUser;
import com.icheer.stock.system.user.service.UserService;
import com.icheer.stock.system.user.service.WxLoginService;
import com.icheer.stock.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api")
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class AppBusinessController  extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AppBusinessController.class);





    @Resource
    private UserService userService;

    @Resource
    private WxLoginService wxLoginService;



    @Resource
    private StockDataService stockDataService;

    @Resource
    private StockInfoService stockInfoService;




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
        List<StockData> dataList = stockDataService.list();
        System.out.println(getDataTable(dataList));
        return new Result(200,"",getDataTable(dataList));
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
     * @param stockKey
     * By   个股代码 or 名称
     *
     */
    @RequestMapping("/search_stockInfo")
    @ResponseBody
    public Result search_stockInfo(@RequestBody StockInfo stockKey){

        startPage();
        ExcludeEmptyQueryWrapper<StockInfo> stockQuery = new ExcludeEmptyQueryWrapper<>();
        stockQuery.eq("deleted",0);
        if (stockKey.getCode()!=null)
        {
            stockQuery.eq("code",stockKey.getCode());
        }else
        {stockQuery.like("name",stockKey.getName());}
        List<StockInfo> stocks= stockInfoService.list(stockQuery);
        /**及其100日数据**/

        return  new Result(200,"success",getDataTable(stocks));

    }


}
