package com.icheer.stock.system.tradeData.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StockSimilar{

    private  String code;
    private  String name;
    private  double similar;
    private  double change;
    private List<TradeData> tradeData;
    private LocalDate lastDate;
    private LocalDate startDate;
}
