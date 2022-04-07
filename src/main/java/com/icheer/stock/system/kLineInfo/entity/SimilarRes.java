package com.icheer.stock.system.kLineInfo.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SimilarRes {
    private String tableName;
    private ArrayList<Integer> startIDList;
    private String matchLetter;
}
