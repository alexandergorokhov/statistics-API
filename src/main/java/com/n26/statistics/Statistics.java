package com.n26.statistics;

import com.n26.pojo.Metric;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Component
public class Statistics {

    private TransactionStatistics transactionStatistics;


    @Autowired
    public Statistics(TransactionStatistics transactionStatistics) {
        this.transactionStatistics = transactionStatistics;
    }

    public Metric getStatistics() {
        List<TransactionBucket> bucketList = transactionStatistics.getValidTransactionsBuckets(Instant.now().toEpochMilli());

        Metric statistics = new Metric();
        statistics.reset();
        bucketList.forEach(bucket -> bucket.collectStatistics(statistics));
        if (statistics.getCount().equals(0L)) {
            statistics.setMax(new BigDecimal(0.00).setScale(2));
            statistics.setMin(new BigDecimal(0.00).setScale(2));
        }

        return statistics;

    }

}
