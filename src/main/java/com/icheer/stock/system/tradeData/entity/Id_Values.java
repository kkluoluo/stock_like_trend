package com.icheer.stock.system.tradeData.entity;

import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.data.relational.core.sql.In;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Id_Values {

    Integer id;

    Double  ma5;

    LocalDate tradeDate;
}
