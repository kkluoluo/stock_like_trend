package com.icheer.stock.controller;


import com.icheer.stock.system.kLineInfo.entity.KLineInfo;
import com.icheer.stock.system.kLineInfo.entity.SimilarRes;
import com.icheer.stock.system.kLineInfo.entity.testRes;
import com.icheer.stock.system.processedTabel.entity.ProcessedTable;
import com.icheer.stock.system.processedTabel.service.ProcessedTableService;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.util.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping("/test")
public class KLineSimilarController {

    @Resource
    private ProcessedTableService processedTableService;

    @Resource
    private TradeDataService tradeDataService;


    @RequestMapping("/getKLineSimilar")
    @ResponseBody
    public Result getKLineSimilar(KLineInfo kLineInfo) {
        String tableName = kLineInfo.getCode();
        String tradeDate = kLineInfo.getTradeDate();
        // 获取表格数据
        String  tableNameProcessed = tableName + "_processed";
        List<TradeData> listTradeData = tradeDataService.list(tableName);
        List<ProcessedTable> listProcessedTable = processedTableService.list(tableNameProcessed);



        return new Result(555,"ada","");
    }

    @RequestMapping("/hello")
    public Result hello() {
        String tsCode = "000001_sz";
        LocalDate date = LocalDate.of(2018,5,16);
        // 从0开始
        List<ProcessedTable> listProcessedTable = processedTableService.list("processed_" + tsCode);
        List<TradeData> listTradeData = tradeDataService.list(tsCode);
        TradeData endTradeData = listTradeData.stream()
                .filter(tradeData -> date.equals(tradeData.getTradeDate()))
                .findAny()
                .orElse(null);
        assert endTradeData != null;
//        6410
        int endIndex = endTradeData.getId();
        int startIndex = endIndex - 29;
//        ProcessedTable startProcessedTable = listProcessedTable.stream()
//                .filter(processedTable ->startIndex.equals(processedTable.getIniPoint()))
//                .findAny()
        ListIterator<ProcessedTable> iterator = listProcessedTable.listIterator();
        int startPoint = 0;
        int startPointIndex = 0;
        int endPoint = 0;
        int endPointIndex = 0;
//       遍历
        while (iterator.hasNext()) {
            ProcessedTable element = iterator.next();
            if (element.getIniPoint() < startIndex && element.getCurPoint() > startIndex) {
                startPoint = element.getIniPoint();
                startPointIndex = element.getId();
            }
            if (element.getIniPoint() < endIndex && element.getCurPoint() > endIndex) {
                endPoint = element.getCurPoint();
                endPointIndex = element.getId();
                break;
            }
        }

        double[] ma5Trend = new double[endPointIndex-startPointIndex+1];
//        StringBuilder ma5TrendLetter = "haha";
        StringBuilder ma5TrendLetter = new StringBuilder();
        double[] ma5Slope = new double[endPointIndex-startPointIndex+1];
        double[] pointDelta = new double[endPointIndex-startPointIndex+1];

        for (int i = startPointIndex, j = 0; i < endPointIndex + 1; i++, j++) {
//            行
            ProcessedTable processedTable = listProcessedTable.get(i-1);
            ma5Slope[j] = processedTable.getMa5Slope();
            ma5Trend[j] = processedTable.getMa5Trend();
            pointDelta[j] = processedTable.getPointDelta();
            ma5TrendLetter.append(processedTable.getMa5TrendLetter());
        }
        // 匹配字符串长度
        int matchLen = ma5TrendLetter.length();
        // 匹配日期区间长度
        int pointLen = endPoint - startPoint;
        ArrayList<String> listHS300 = processedTableService.listHS300();
        ArrayList<SimilarRes> similarRes = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            String tableName = listHS300.get(i).toLowerCase().replace('.', '_');
            String tableNameProcessed = "processed_" + tableName;
            List<ProcessedTable> listMatch = processedTableService.list(tableNameProcessed);

            int arraySize = listMatch.size();
            double[] ma5SlopeMatch = new double[arraySize];
            double[] ma5TrendMatch = new double[arraySize];
            double[] pointDeltaMatch = new double[arraySize];
            StringBuilder letterMatch = new StringBuilder();

            for (int j = 0; j < arraySize; j++) {
                ProcessedTable processedTable = listMatch.get(j);
                if (processedTable == null){
                    continue;
                }
                ma5SlopeMatch[j] = processedTable.getMa5Slope();
                ma5TrendMatch[j] = processedTable.getMa5Trend();
                pointDeltaMatch[j] = processedTable.getPointDelta();
                letterMatch.append(processedTable.getMa5TrendLetter());
            }

            // 当前表 匹配的 索引集合 startIndex(ID)
            ArrayList<Integer> indexMatchList = searchAllIndex(letterMatch.toString(),ma5TrendLetter.toString());
            ArrayList<Integer> startIDMatchList = new ArrayList<Integer>();
            for (int j = 0; j < indexMatchList.size(); j++) {
                int startID = indexMatchList.get(j);
                boolean flag = true;
                int pointLenMatch = listMatch.get(startID+matchLen-1).getCurPoint() - listMatch.get(startID).getIniPoint();
                double longMatch = (double) pointLenMatch/pointLen;
//                double longMatch = (double) pointLenMatch/matchLen;

                for (int k = 0; k < matchLen; k++) {
                    // 斜率控制
                    if (ma5SlopeMatch[startID+k] == 0){
                        continue;
                    }
                    if (ma5SlopeMatch[startID+k] / ma5Slope[k] < 0.9){
                        flag = false;
                        break;
                    }
                    // 日期区间长度控制
                    if ((pointDeltaMatch[startID+k] / pointDelta[k] / longMatch) < 0.8){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    // 存放了对应startID的原始数据表的id 数据库中id从1开始自增
                    startIDMatchList.add(startID+1);
                }
            }
            if (!startIDMatchList.isEmpty()){
                SimilarRes res = new SimilarRes();
                res.setTableName(tableName);
                res.setStartIDList(startIDMatchList);
                res.setMatchLetter(ma5TrendLetter.toString());
                similarRes.add(res);
            }
        }


        return new Result(555, "haha", similarRes);
    }

    /**
     * 改进版
     * @return /
     */

    @RequestMapping("/getSimilarRes")
    public Result getSimilarRes() {
        String tsCode = "000001_sz";
        // 最后一天
        LocalDate date = LocalDate.of(2018,5,16);
        // 从0开始
        List<ProcessedTable> listProcessedTable = processedTableService.list("processed_" + tsCode);
        List<TradeData> listTradeData = tradeDataService.listData(tsCode);
        TradeData endTradeData = listTradeData.stream()
                .filter(tradeData -> date.equals(tradeData.getTradeDate()))
                .findAny()
                .orElse(null);
        assert endTradeData != null;
//        6410
        int endIndex = endTradeData.getId();
        int startIndex = endIndex - 29;

        ListIterator<ProcessedTable> iterator = listProcessedTable.listIterator();
        int startPointIndex = 0;
        int endPointIndex = 0;
//       遍历
        while (iterator.hasNext()) {
            ProcessedTable element = iterator.next();
            if (element.getIniPoint() < startIndex && element.getCurPoint() > startIndex) {
                startPointIndex = element.getId();
            }
            if (element.getIniPoint() < endIndex && element.getCurPoint() > endIndex) {
                endPointIndex = element.getId();
                break;
            }
        }

        StringBuilder ma5TrendLetter = new StringBuilder();
        double[] ma5Radian = new double[endPointIndex-startPointIndex+1];
        double[] pointDelta = new double[endPointIndex-startPointIndex+1];

        for (int i = startPointIndex, j = 0; i < endPointIndex + 1; i++, j++) {

            ProcessedTable processedTable = listProcessedTable.get(i-1);
            ma5Radian[j] = processedTable.getMa5Radian();
            pointDelta[j] = processedTable.getPointDelta();
            ma5TrendLetter.append(processedTable.getMa5TrendLetter());
        }
        // 匹配字符串长度
        int matchLen = ma5TrendLetter.length();

        ArrayList<String> listHS300 = processedTableService.listHS300();

        double[] similarities = new double[10];
        String[] tableNameRes = new String[10];
        int[] startIDRes = new int[10];
        for (int i = 0; i < 300; i++) {
            String tableName = listHS300.get(i).toLowerCase().replace('.', '_');
            String tableNameProcessed = "processed_" + tableName;
            List<ProcessedTable> listMatch = processedTableService.list(tableNameProcessed);

            int arraySize = listMatch.size();
            double[] ma5RadianMatch = new double[arraySize];
            double[] pointDeltaMatch = new double[arraySize];
            StringBuilder letterMatch = new StringBuilder();

            for (int j = 0; j < arraySize; j++) {
                ProcessedTable processedTable = listMatch.get(j);
                if (processedTable == null){
                    continue;
                }
                ma5RadianMatch[j] = processedTable.getMa5Radian();
                pointDeltaMatch[j] = processedTable.getPointDelta();
                letterMatch.append(processedTable.getMa5TrendLetter());
            }

            // 当前表 匹配的 索引集合 startIndex(ID)
            ArrayList<Integer> indexMatchList = searchAllIndex(letterMatch.toString(),ma5TrendLetter.toString());


            for (int startID : indexMatchList) {
                double radianSimilar = 0;
                double deltaRatioExcept = 0;

                for (int k = 0; k < matchLen; k++) {
                    if (ma5RadianMatch[startID + k] == 0) {
                        continue;
                    }
                    double radianDiffRatio = Math.abs(ma5RadianMatch[startID + k] - ma5Radian[k]) / (0.5 * Math.PI);

                    // TODO: 2022/3/31 解决delta归一化
                    deltaRatioExcept += pointDeltaMatch[startID + k] / pointDelta[k];
                    radianSimilar += 1 - radianDiffRatio;
                }
                deltaRatioExcept = deltaRatioExcept / matchLen;
                double deltaSimilar = 0;
                for (int k = 0; k < matchLen; k++) {
                    double deltaDiffRatio = Math.abs(pointDeltaMatch[startID + k] / pointDelta[k] - deltaRatioExcept) / deltaRatioExcept;
                    if (deltaDiffRatio > 2) {
                        deltaSimilar += 0;
                    } else {
                        deltaSimilar += 1 - deltaDiffRatio;
                    }
                }
                // 权重分配
                double weight = 0.7;
                // 相似度计算
                double similarity = (weight * radianSimilar + (1 - weight) * deltaSimilar) / matchLen;
                if (similarity <= similarities[9]) {
                    continue;
                }
                boolean flag = true;
                for (int k = 0; k < similarities.length - 1; k++) {
                    if (similarity > similarities[k]) {
                        for (int l = k; l < similarities.length - 1; l++) {
                            similarities[k + 1] = similarities[k];
                            //todo 结果收集
                            tableNameRes[k + 1] = tableNameRes[k];
                            startIDRes[k + 1] = startIDRes[k];
                        }
                        similarities[k] = similarity;
                        tableNameRes[k] = tableName;
                        startIDRes[k] = startID + 1;
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    similarities[9] = similarity;
                    tableNameRes[9] = tableName;
                    startIDRes[9] = startID + 1;
                }
            }
//            if (!startIDMatchList.isEmpty()){
//                SimilarRes res = new SimilarRes();
//                res.setTableName(tableName);
//                res.setStartIDList(startIDMatchList);
//                res.setMatchLetter(ma5TrendLetter.toString());
//                similarRes.add(res);
//            }
        }
        testRes testRes = new testRes();
        testRes.setSimilarities(similarities);
        testRes.setStartIDRes(startIDRes);
        testRes.setTableNameRes(tableNameRes);


        return new Result(555, "haha", testRes);
    }
    /**
     * 返回sub_str在main_str中出现的所有索引集合
     * @param main_str 主串
     * @param sub_str  搜索Key
     * @return 所有索引集合
     */
    public ArrayList<Integer> searchAllIndex(String main_str, String sub_str) {
        int a = main_str.indexOf(sub_str);//*第一个出现的索引位置
        ArrayList<Integer> res = new ArrayList<>();
        while (a != -1) {
            res.add(a);
            a = main_str.indexOf(sub_str, a + 1);//*从这个索引往后开始第一个出现的位置
        }
        return res;
    }

}


