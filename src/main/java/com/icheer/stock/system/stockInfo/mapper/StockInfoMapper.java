package com.icheer.stock.system.stockInfo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.mapper.TradeDataProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;


@Mapper
public interface StockInfoMapper extends BaseMapper<StockInfo> {

    @SelectProvider(type= selectProvider.class,method="CSI300list")
    public List<StockInfo> getCsi300();

    @SelectProvider(type= selectProvider.class,method="CSI300list_ts_code_name")
    public List<StockInfo> getCsi300_ts_code_name();

    @SelectProvider(type= selectProvider.class,method="getByCode")
    public StockInfo getByCode(String  code );

    @SelectProvider(type= selectProvider.class,method="getTsByCode")
    public String  getTsByCode(String  code );

    @SelectProvider(type= selectProvider.class,method="listByName")
    public List<StockInfo> listByName(String  name );
}