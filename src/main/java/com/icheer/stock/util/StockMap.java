package com.icheer.stock.util;

import lombok.Data;

@Data
public class StockMap {

    //code搜索
    private String code;
    //字符搜索
    private String name;
    //比较k线范围
    private int    range;
    //预测的k线范围
    private int    preRange;

}
