package com.icheer.stock.system.processedTabel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.icheer.stock.system.processedTabel.entity.ProcessedTable;
import com.icheer.stock.system.processedTabel.entity.TestProcessedRes;
import com.icheer.stock.system.processedTabel.mapper.ProcessedTableMapper;
import com.icheer.stock.system.processedTabel.service.ProcessedTableService;
import com.icheer.stock.system.stockInfo.entity.StockInfo;
import com.icheer.stock.system.stockInfo.service.StockInfoService;
import com.icheer.stock.system.tradeData.entity.StockSimilar;
import com.icheer.stock.system.tradeData.entity.TradeData;
import com.icheer.stock.system.tradeData.service.TradeDataService;
import com.icheer.stock.util.StockMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@Service
public class ProcessedTableServiceImpl extends ServiceImpl<ProcessedTableMapper, ProcessedTable> implements ProcessedTableService {

    @Resource
    private ProcessedTableMapper processedTableMapper;

    @Resource
    private TradeDataService tradeDataService;

    @Resource
    private StockInfoService stockInfoService;

    @Override
    public ArrayList<ProcessedTable> list(String table_name) {
        return processedTableMapper.list(table_name);
    }

    @Override
    public ArrayList<String> listHS300() {
        return processedTableMapper.listHS300();
    }

    @Override
    public ArrayList<ProcessedTable> listDescById(String table_name) {
        return processedTableMapper.listDescById(table_name);
    }

    @Override
    public List<StockSimilar> getKLineSimilar(StockMap stockMap) {
        // 计时
        long startTime = System.currentTimeMillis();
        // tsCode = code + 交易所缩写 例如000001_sz
        String tsCode = tradeDataService.tableName_code(stockMap.getCode());
        int range = stockMap.getRange();
        // 获取所选股票的原始数据
        int endIndex = tradeDataService.getTableSize(tsCode);
        int startIndex = endIndex - range-1;
        // 获取所选股票的processed数据
        List<ProcessedTable> listProcessedTable = list("processed_" + tsCode);
        ListIterator<ProcessedTable> iterator = listProcessedTable.listIterator();
        int startPointIndex = 0;
        int endPointIndex = 0;
        //遍历确定对应processed数据中的起始id和终点id
        //Todo 可以增加倒序排序快取startPointIndex
        while (iterator.hasNext()) {
            ProcessedTable element = iterator.next();
            if (element.getIniPoint() <= startIndex && element.getCurPoint() >= startIndex) {
                startPointIndex = element.getId();
            }
            if (element.getIniPoint() <= endIndex && element.getCurPoint() >= endIndex) {
                endPointIndex = element.getId();
                break;
            }
        }

        StringBuilder ma5TrendLetter = new StringBuilder();
        double[] ma5Radian = new double[endPointIndex-startPointIndex+1];
        double[] pointDelta = new double[endPointIndex-startPointIndex+1];
        //遍历processed表获取特征数据
        for (int i = startPointIndex, j = 0; i < endPointIndex + 1; i++, j++) {

            ProcessedTable processedTable = listProcessedTable.get(i-1);
            ma5Radian[j] = processedTable.getMa5Radian();
            pointDelta[j] = processedTable.getPointDelta();
            ma5TrendLetter.append(processedTable.getMa5TrendLetter());
        }
        // 匹配字符串长度
        int matchLen = ma5TrendLetter.length();

        //aShares代码以及中文名列表
        ArrayList<StockInfo> aSharesInfo = stockInfoService.getCodeAndName();

        //设置输入code对应的原始数据
        StockSimilar stockSimilarOrigin = new StockSimilar();
        stockSimilarOrigin.setSimilar(1);
        stockSimilarOrigin.setCode(stockMap.getCode());
        stockSimilarOrigin.setName(aSharesInfo.stream().filter(s -> s.getCode().equals(stockMap.getCode())).collect(Collectors.toList()).get(0).getName());
        stockSimilarOrigin.setTradeData(tradeDataService.getTradeSinceId(stockMap.getCode(),listProcessedTable.get(startPointIndex).getIniPoint(), listProcessedTable.get(listProcessedTable.size()-1).getCurPoint()-listProcessedTable.get(startPointIndex).getIniPoint()));
//        setStockSimilarInfo(stockSimilarOrigin);
        TradeData startTradeDataOrigin = stockSimilarOrigin.getTradeData().get(0);
        TradeData lastTradeDataOrigin = stockSimilarOrigin.getTradeData().get(stockSimilarOrigin.getTradeData().size()-1);
        stockSimilarOrigin.setStartDate(startTradeDataOrigin.getTradeDate());
        stockSimilarOrigin.setLastDate(lastTradeDataOrigin.getTradeDate());

        //CSI300代码列表
        ArrayList<String> listHS300 = listHS300();

        //相似度结果
        double[] similarities = new double[10];
        //相似code结果
        String[] codeRes = new String[10];
        //相似结果在对应code的表中的起始ID
        int[] startIdOriginRes = new int[10];
        //相似结果的天数（range）
        int[] originIdRangeRes = new int[10];

        //测试用结果
        String[] tableNameRes = new String[10];
        int[] startIDRes = new int[10];


        //计时
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");

        //测试循环次数
        int matchSum = 0;
        for (int i = 0; i < 300; i++) {
            //CSI300对应表名
            String tableName = listHS300.get(i).toLowerCase().replace('.', '_');
            String tableNameProcessed = "processed_" + tableName;
            //获取CSI300数据
            List<ProcessedTable> listMatch = list(tableNameProcessed);

            int arraySize = listMatch.size();
            double[] ma5RadianMatch = new double[arraySize];
            double[] pointDeltaMatch = new double[arraySize];
            StringBuilder letterMatch = new StringBuilder();
            //遍历processed表获取特征数据
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

            // 循环次数
            matchSum += indexMatchList.size();
            for (int startID : indexMatchList) {
                int pointLenMatch = listMatch.get(startID+matchLen-1).getCurPoint() - listMatch.get(startID).getIniPoint();

                // 弧度相似度=(1-弧度差值的百分比)*日期长度的权重
                double radianSimilar = 0;
                // 日期长度成比例的比例期望 e.g. A：1，2，3 B：2，3，4 deltaRatioExcept=(2/1+3/2+4/3)/3
                double deltaRatioExcept = 0;

                for (int k = 0; k < matchLen; k++) {
                    if (ma5RadianMatch[startID + k] == 0) {
                        continue;
                    }
                    // 弧度差值的百分比
                    double radianDiffRatio = Math.abs(ma5RadianMatch[startID + k] - ma5Radian[k]) / (0.5 * Math.PI);
                    // 日期长度的权重
                    double deltaWeight = pointDeltaMatch[startID + k]/pointLenMatch;
                    deltaRatioExcept += pointDeltaMatch[startID + k] / pointDelta[k];
                    radianSimilar += (1 - radianDiffRatio)*deltaWeight;
                }
                deltaRatioExcept = deltaRatioExcept / matchLen;
                //日期长度成比例的程度
                double deltaSimilar = 0;
                for (int k = 0; k < matchLen; k++) {
                    // 日期长度成比例的比例偏离值 = |(每段日期长度的比值-日期长度成比例的比例期望)|/日期长度成比例的比例期望
                    double deltaDiffRatio = Math.abs(pointDeltaMatch[startID + k] / pointDelta[k] - deltaRatioExcept) / deltaRatioExcept;
                    //Todo 由于比例偏离值无上界，人为设置偏离值大于2时，日期长度不成比例，数值待测试改进。
                    if (deltaDiffRatio > 2) {
                        deltaSimilar += 0;
                    } else {
                        deltaSimilar += 1 - deltaDiffRatio;
                    }
                }
                // 权重分配
                double weight = 0.7;
                // 最终相似度计算
                double similarity = (weight * radianSimilar) + (((1 - weight) * deltaSimilar) / matchLen);

                //跳过自身结果
                if(similarity ==1){
                    continue;
                }

                //Todo 还未删去输入的结果，由于输入结果的相似度一定是百分百，准备保存11个结果，输出后10个。
                //保存相似度最高的10个结果
                if (similarity <= similarities[9]) {
                    continue;
                }
                boolean flag = true;
                int similarLength = similarities.length - 1;
                for (int k = 0; k < similarLength; k++) {
                    if (similarity > similarities[k]) {
                        for (int l = k; l < similarities.length - 1; l++) {
                            similarities[k + 1] = similarities[k];
                            codeRes[k + 1] = codeRes[k];
                            startIdOriginRes[k + 1] = startIdOriginRes[k];
                            originIdRangeRes[k+1] = originIdRangeRes[k];

                            //测试用tableName
                            tableNameRes[k + 1] = tableNameRes[k];
                            startIDRes[k + 1] = startIDRes[k];
                        }
                        similarities[k] = similarity;
                        codeRes[k] = tableName.substring(0,6);
                        startIdOriginRes[k] = listMatch.get(startID).getIniPoint();
                        originIdRangeRes[k] = listMatch.get(startID + matchLen -1).getCurPoint() - startIdOriginRes[k];
                        //测试用tableName
                        tableNameRes[k] = tableName;
                        startIDRes[k] = startID + 1;

                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    similarities[9] = similarity;
                    codeRes[9] = tableName.substring(0,6);
                    startIdOriginRes[9] = listMatch.get(startID).getIniPoint();

                    originIdRangeRes[9] = listMatch.get(startID + matchLen -1).getCurPoint() - startIdOriginRes[9];
                    //测试用tableName
                    tableNameRes[9] = tableName;
                    startIDRes[9] = startID + 1;
                }
            }
        }
        endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        //输出结果
        List<StockSimilar> similarList = new ArrayList<>();
        similarList.add(stockSimilarOrigin);
        for (int i = 0; i < 10; i++) {
            StockSimilar stockSimilar = new StockSimilar();
            stockSimilar.setCode(codeRes[i]);
            stockSimilar.setSimilar(similarities[i]);
            stockSimilar.setName(aSharesInfo.stream().filter(s -> s.getCode().equals(stockSimilar.getCode())).collect(Collectors.toList()).get(0).getName());
            stockSimilar.setTradeData(tradeDataService.getTradeSinceId(codeRes[i],startIdOriginRes[i], originIdRangeRes[i]+stockMap.getPreRange()));
            TradeData startTradeData = stockSimilar.getTradeData().get(0);
            TradeData lastTradeData = stockSimilar.getTradeData().get(stockSimilar.getTradeData().size()-1-stockMap.getPreRange());
            TradeData preTradeData = stockSimilar.getTradeData().get(stockSimilar.getTradeData().size()-1);
            stockSimilar.setStartDate(startTradeData.getTradeDate());
            stockSimilar.setLastDate(lastTradeData.getTradeDate());
            stockSimilar.setChange((preTradeData.getClose()-lastTradeData.getClose())/lastTradeData.getClose());
            similarList.add(stockSimilar);
        }

        //绘图测试结果
        TestProcessedRes testProcessedRes = new TestProcessedRes();
        testProcessedRes.setSimilarities(similarities);
        testProcessedRes.setStartIDRes(startIDRes);
        testProcessedRes.setTableNameRes(tableNameRes);
        System.out.println(testProcessedRes);
        System.out.println(matchLen);
        endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        System.out.println(matchSum);

        return similarList;
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

