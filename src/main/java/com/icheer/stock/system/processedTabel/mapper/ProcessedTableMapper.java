package com.icheer.stock.system.processedTabel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.icheer.stock.system.processedTabel.config.ProcessedTableProvider;
import com.icheer.stock.system.processedTabel.entity.ProcessedTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.ArrayList;

@Mapper
public interface ProcessedTableMapper extends BaseMapper<ProcessedTable> {
    @SelectProvider(type= ProcessedTableProvider.class,method="list")
    public ArrayList<ProcessedTable> list(String table_name);

//    @SelectProvider(type= ProcessedTableProvider.class,method="listHS300")
    @Select("select ts_code from a_shares where csi300_flag = 1")
    public ArrayList<String> listHS300();

}
