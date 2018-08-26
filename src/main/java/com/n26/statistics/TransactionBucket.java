package com.n26.statistics;

import com.n26.pojo.Metric;
import com.n26.pojo.Transaction;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Getter
public class TransactionBucket {
    private Metric metric;
    private Long timeStamp;
    private final ReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;

    public TransactionBucket() {
        this.metric = new Metric();
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
        this.reset();

    }

    public void create(Transaction transaction) {
        metric.setCount(1L);
        metric.setSum(transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        metric.setMin(transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        metric.setMax(transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        metric.setAvg(transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        timeStamp = transaction.getTimestamp().getTime();
    }

    public void update(Transaction transaction) {

        BigDecimal amount = transaction.getAmount();
        metric.setCount(metric.getCount() + 1);
        metric.setSum(metric.getSum().add(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
        metric.setMin(metric.getMin().min(amount).setScale(2));
        metric.setMax(metric.getMax().max(amount).setScale(2));
        metric.setAvg(metric.getSum().divide(new BigDecimal(metric.getCount()).setScale(2, BigDecimal.ROUND_HALF_UP),2,BigDecimal.ROUND_HALF_UP));
        timeStamp = transaction.getTimestamp().getTime();
    }


    public void reset(){
        metric.reset();
        this.timeStamp = 0L;
    }

    public boolean isEmpty(){
        return this.metric.getCount().equals(0L);
    }

    public void addTransaction(Transaction transaction) {
        this.metric.setSum(this.metric.getSum().add(transaction.getAmount()).setScale(2, RoundingMode.HALF_UP) );
        this.metric.setCount(this.metric.getCount() + 1);
        this.metric.setAvg(this.metric.getSum().divide(new BigDecimal(this.metric.getCount()).setScale(2),2,BigDecimal.ROUND_HALF_UP));
        this.metric.setMin(metric.getMin().min(transaction.getAmount()));
        this.metric.setMax(metric.getMax().max(transaction.getAmount()));

    }

    public void collectStatistics(Metric metricResult){
        try {
            this.readLock.lock();
            metricResult.setSum(metricResult.getSum().add(this.metric.getSum()).setScale(2,RoundingMode.HALF_UP));
            metricResult.setCount(metricResult.getCount()+this.metric.getCount());
            metricResult.setAvg(metricResult.getSum().divide(new BigDecimal(metricResult.getCount()).setScale(2),2,BigDecimal.ROUND_HALF_UP));
            metricResult.setMax(metricResult.getMax().max(this.metric.getMax()));
            metricResult.setMin(metricResult.getMin().min(this.metric.getMin()));
        }finally {
            this.readLock.unlock();
        }
    }
}
