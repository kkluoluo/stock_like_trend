package com.icheer.stock.system.tradeData.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.mapper.TradeDataMapper;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeDataServiceImpl extends ServiceImpl<TradeDataMapper, TradeData> implements TradeDataService {
    private static final Logger log = LoggerFactory.getLogger(com.icheer.stock.system.tradeData.service.impl.TradeDataServiceImpl.class);

    @Autowired
    private  TradeDataMapper tradeDataMapper;

    /**get list By table_name */
    public List<TradeData> list(String table_name){
       return tradeDataMapper.list(table_name);
    }

    /**倒序 最新 100 条记录 */
    public List<TradeData> listDescByTradeDate(String table_name, Integer range){
        return tradeDataMapper.listDescByTradeDate(table_name,range);
    }
}



