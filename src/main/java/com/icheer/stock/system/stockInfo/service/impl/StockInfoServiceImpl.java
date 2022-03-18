package com.icheer.stock.system.stockInfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.system.stockData.entity.StockData;
import com.icheer.stock.system.stockData.mapper.StockDataMapper;
import com.icheer.stock.system.stockData.service.StockDataService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.mapper.StockInfoMapper;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class StockInfoServiceImpl extends ServiceImpl<StockInfoMapper, StockInfo> implements StockInfoService {
    private static final Logger log = LoggerFactory.getLogger(com.icheer.stock.system.stockInfo.service.impl.StockInfoServiceImpl.class);

}

