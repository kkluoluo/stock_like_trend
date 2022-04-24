package com.icheer.stock.system.processedTabel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.icheer.stock.system.processedTabel.entity.ProcessedTable;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.util.StockMap;

import java.util.ArrayList;
import java.util.List;

public interface ProcessedTableService extends IService<ProcessedTable> {
    /**get list By table_name */
    ArrayList<ProcessedTable> list(String table_name);

    ArrayList<String> listHS300();

    List<StockSimilar> getKLineSimilar(StockMap stockMap);
}
