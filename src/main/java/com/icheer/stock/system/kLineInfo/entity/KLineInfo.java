package com.icheer.stock.system.kLineInfo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class KLineInfo implements Serializable {

    private String code;

    private String tradeDate;
}
