package com.icheer.stock.system.processedTabel.config;

import org.apache.ibatis.jdbc.SQL;


public class ProcessedTableProvider {

    public String listByColumns(String table_name, String columns){

        return new SQL(){{
            SELECT(columns);
            FROM(table_name);


        }}.toString();
    }
    public String list(String table_name){

        return new SQL(){{
            SELECT("*");
            FROM(table_name);


        }}.toString();
    }

//    获取沪深300code list
    public String listHS300(){
        return "select code from hs300Code";
    }

}
