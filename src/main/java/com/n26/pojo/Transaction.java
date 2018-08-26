package com.n26.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Transaction {

    private BigDecimal amount;
    private Timestamp timestamp;
}
