package com.icheer.stock.controller;


import com.icheer.stock.system.processedTabel.service.ProcessedTableService;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.util.Result;
import com.icheer.stock.util.StockMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/test")
public class KLineSimilarController {

    @Resource
    private ProcessedTableService processedTableService;

    /**
     * K线相似
     * @param stockMap /
     * @return /
     */
    @RequestMapping("/getSimilarRes")
    public Result getSimilarRes(@RequestBody StockMap stockMap) {
        List<StockSimilar> similarList = processedTableService.getKLineSimilar(stockMap);
        return new Result(200, "", similarList);
    }
}


