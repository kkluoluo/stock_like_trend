package com.icheer.stock.system.user.mapper.stockInfo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 个股信息
 *
 * @author luoxiaoying
 * @create 2022-03-18 11:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("a_shares")
@ApiModel(value ="a_shares",description = "个股信息表")
public class StockInfo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    /** 股票代码*/
    private String code;
    /** 股票代码*/
    private String tsCode;
    /** 股票简称*/
    private String name;
    /** 总股本*/
    private String totalShare;
    /** 流通股*/
    private String floatShare;
    /**所属行业 */
    private String industry;
    /** 上市日期*/
    private LocalDate listDate;
    /** 总市值*/
    private String totalMv;
    /** 流通市值*/
    private String circMv;
    /** 板块*/
    private String market;
    /** 沪深300权重股标志位*/
    private String csi300Flag;
    /** 沪深300权重股--权重*/
    private double csi300Weight;
    /** 删除标志位*/
    private String deleted;
    /** 更新时间*/
    private LocalDateTime updateTime;
}
