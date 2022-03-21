package com.icheer.stock.system.tradeData.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icheer.stock.system.tradeData.entity.TradeData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface TradeDataMapper extends BaseMapper<TradeData> {

    @SelectProvider(type=TradeDataProvider.class,method="list")
    public List<TradeData> list(String table_name);

    @SelectProvider(type=TradeDataProvider.class,method="listDescByTradeDate")
    public List<TradeData> listDescByTradeDate(String table_name, Integer range);

}
