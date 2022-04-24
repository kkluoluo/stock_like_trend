package com.icheer.stock.system.processedTabel.config;

import org.apache.ibatis.jdbc.SQL;


public class ProcessedTableProvider {
    public String list(String table_name){
        return "select id,iniPoint,curPoint,ma5Delta,pointDelta,ma5TrendLetter,ma5Radian from "+ table_name;
    }

    //获取沪深300code list
    public String listHS300(){
        return "select code from hs300Code";
    }
}
