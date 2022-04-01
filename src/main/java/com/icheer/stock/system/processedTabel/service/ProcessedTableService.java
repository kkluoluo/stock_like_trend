package com.icheer.stock.system.processedTabel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.processedTabel.entity.ProcessedTable;

import java.util.ArrayList;

public interface ProcessedTableService extends IService<ProcessedTable> {
    /**get list By table_name */
    public ArrayList<ProcessedTable> list(String table_name);

    public ArrayList<String> listHS300();
}
