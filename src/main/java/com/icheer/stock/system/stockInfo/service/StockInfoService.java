package com.icheer.stock.system.stockInfo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.util.PageDomain;

import java.util.ArrayList;
import java.util.List;

public interface StockInfoService extends IService<StockInfo> {


    /** name模糊查找 */
    public IPage<StockInfo> listByName(String name, IPage<StockInfo> stockInfoIPage );

    /**getOneByCode */
    public  StockInfo  getOneByCode(String code);

    /**获取沪深300的股票list */
    public List<StockInfo> getCSI300List();

    /**获取所有股票的code和name */
    public ArrayList<StockInfo> getCodeAndName();
}
