package com.n26.pojo;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Metric {
    private BigDecimal sum;


    private BigDecimal avg;
    private BigDecimal max;
    private BigDecimal min;
    private Long count;

    public void reset() {
        sum = new BigDecimal(0.00).setScale(2);
        avg = new BigDecimal(0.00).setScale(2);
        max = new BigDecimal(Long.MIN_VALUE).setScale(2);
        min = new BigDecimal(Long.MAX_VALUE).setScale(2);
        count = 0L;

    }
}
