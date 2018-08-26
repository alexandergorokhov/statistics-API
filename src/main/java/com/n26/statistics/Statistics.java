package com.n26.statistics;

import lombok.Getter;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
public class StatisticsImpl implements Statistics {

    private BigDecimal sum;
    private BigDecimal avg;
    private BigDecimal min;
    private BigDecimal max;
    private Long count;

    private static Statistics statistics;

    public static Statistics getStatisticsInstance() {
        if (Objects.isNull(statistics)) {
            synchronized (StatisticsImpl.class) {
                statistics = new StatisticsImpl();
            }
        }
        return statistics;
    }

    private StatisticsImpl() {
        resetStatistics();

    }
    @Scheduled(fixedRate = 60000)
    public void resetStatistics() {
        sum = new BigDecimal(0);
        avg = new BigDecimal(0);
        min = new BigDecimal(Integer.MAX_VALUE);
        max = new BigDecimal(Integer.MIN_VALUE);
        count = 0L;
    }


    @Override
    public synchronized void update(BigDecimal amount) {
        count++;
        sum = sum.add(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
        min = min.min(amount);
        max = max.max(amount);
        avg = sum.divide(new BigDecimal(count).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

}
