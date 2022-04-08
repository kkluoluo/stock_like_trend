package com.icheer.stock.controller.test.testResult.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("test_result")
@ApiModel(value ="test_result",description = "验证后果表，用户存储结果便于画图")
public class Test {


    @TableId(value = "id", type = IdType.AUTO)
    private int id;


    private String code;

    private String name;

    private String dateId;

    private String mo;

    private String flag;

    private String anCode;

    private Double k;
}
