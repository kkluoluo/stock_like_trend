package com.icheer.stock.system.stockInfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;

import java.util.ArrayList;
import java.util.List;

public interface StockInfoService extends IService<StockInfo> {


    /** name模糊查找 */
    public  List<StockInfo>  listByName(String name);

    /**getOneByCode */
    public  StockInfo  getOneByCode(String code);

    /**获取沪深300的股票list */
    public List<StockInfo> getCSI300List();

    /**获取所有股票的code和name */
    public ArrayList<StockInfo> getCodeAndName();
}
