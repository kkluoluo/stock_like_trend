package com.icheer.stock.system.stockInfo.mapper;

public class selectProvider {



    public String CSI300list(){
        return "select * from a_shares where csi300_flag = '1' and  deleted = '0' ";
    }

    public  String getByCode( String code ){
        return "select * from a_shares where code =  " + code ;
    }

    public  String getTsByCode(String code){
        return "select ts_code from a_shares where code =  " + code ;
    }

    public  String listByName(String name){
        return "select * from a_shares where  deleted ='0' and name like '%" + name+"%' " ;
    }

    public  String CSI300list_ts_code_name(){
        return "select ts_code,code,name from a_shares where csi300_flag = '1' and  deleted = '0' ";
    }
}
