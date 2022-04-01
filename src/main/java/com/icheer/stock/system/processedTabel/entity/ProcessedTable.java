package com.icheer.stock.system.processedTabel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessedTable implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer iniPoint;
    private Integer curPoint;
    private Double ma5Delta;
    private Double ma5Slope;
    private Double ma5Trend;
    private Integer pointDelta;
    private String ma5TrendLetter;
    private Double ma5Radian;
}
