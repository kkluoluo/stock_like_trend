package com.icheer.stock.controller.test.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("test_mo")
@ApiModel(value ="test",description = "test表")
public class Test {


    @TableId(value = "id", type = IdType.AUTO)
    private int id;


    private String code;

    private String name;

    private String dateId;

    private String mo;

    private String anCode;

    private Double k;
}
