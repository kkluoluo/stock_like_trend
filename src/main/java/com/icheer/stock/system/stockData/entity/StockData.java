package com.icheer.stock.system.stockData.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * com.icheer.stock.system.user.entity
 *
 * @author eiker
 * @create 2021-01-18 11:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("000001_sz")
@ApiModel(value ="000001_sz",description = "test")
public class StockData implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private long id;

    private String tsCode;

    private LocalDateTime tradeDate;

    private double open;

    private double high;

    private double low;

    private double close;

    private double preClose;

    private double priceChange;

    private double pctChg;

    private double vol;

    private double amount;

    private double ma5;
    private double maV_5;

    private String deleted;


}
