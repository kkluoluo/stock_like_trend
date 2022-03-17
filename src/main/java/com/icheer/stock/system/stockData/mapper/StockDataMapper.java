package com.icheer.stock.system.stockData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icheer.stock.system.CSI300.entity.CSI300Stock;
import com.icheer.stock.system.stockData.entity.StockData;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface StockDataMapper extends BaseMapper<StockData> {


}