package com.icheer.stock.system.CSI300.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.io.Serializable;
import java.time.LocalDateTime;
/*
<p>
*沪深300
* </p>
*
* @author luoxiao'ying
* @since July 09 2021
*/

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("CSI300")
@ApiModel(value ="CSI300",description = "沪深300表")
public class CSI300Stock implements Serializable {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "indexID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String tsCode;

    private String name;

    private double weight;

    private LocalDateTime updateTime;

    @ApiModelProperty(value = "删除标志位")
    private String deleted;



}

