package com.n26.statistics;

import com.n26.exception.InFutureTransactionException;
import com.n26.exception.OldTransactionException;
import com.n26.pojo.Transaction;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Getter
public class TransactionStatistics {
    private TransactionBucket[] transactionBuckets;

    private static final int AGE = 60000;
    private static final int STEP = 1000;
    private static final int NUMBER_OF_BUCKETS = AGE / STEP;

    public TransactionStatistics() {
        this.transactionBuckets = new TransactionBucket[NUMBER_OF_BUCKETS];
        initBuckets();
    }

    public void initBuckets() {
        for (int i = 0; i < this.transactionBuckets.length; i++) {
            this.transactionBuckets[i] = new TransactionBucket();
        }
    }

    private int getBucketNumber(Transaction transaction) {
        long transactionTime = transaction.getTimestamp().getTime();

        long currentTime = Instant.now().toEpochMilli();

        return (int) ((currentTime - transactionTime) / STEP) % (NUMBER_OF_BUCKETS);
    }

    public void addTransaction(Transaction transaction, long currentTimestamp) throws OldTransactionException, InFutureTransactionException {
        if (!isValidTransaction(transaction.getTimestamp().getTime(), currentTimestamp)) {
            throw new OldTransactionException("Transaction is older than " + AGE + " miliseconds");
        }
        if (isTransactionInTheFuture(transaction.getTimestamp().getTime(), currentTimestamp)) {
            throw new InFutureTransactionException("Transaction is in the future");
        }
        addTransactionToBucket(transaction, currentTimestamp);
    }

    private void addTransactionToBucket(Transaction transaction, Long timestamp) {

        int bucket = getBucketNumber(transaction);
        TransactionBucket transactionBucket = this.transactionBuckets[bucket];

        try {
            transactionBucket.getWriteLock().lock();

            if (transactionBucket.isEmpty()) {
                transactionBucket.create(transaction);
            } else {
                if (isValidTransaction(transaction.getTimestamp().getTime(), timestamp)) {
                    transactionBucket.addTransaction(transaction);
                } else {
                    transactionBucket.reset();
                    transactionBucket.create(transaction);
                }
            }

        } finally {
            transactionBucket.getWriteLock().unlock();
        }
    }

    private boolean isValidTransaction(long metricTimestamp, long currentTimestamp) {

        //if the transaction is in time range
        return metricTimestamp >= currentTimestamp - AGE;
    }

    public void reset() {
        this.initBuckets();
    }

    public List<TransactionBucket> getValidTransactionsBuckets(long currentTimestamp) {
        return Arrays.stream(this.transactionBuckets)
                .filter(bucket -> isValidTransaction(bucket.getTimeStamp(), currentTimestamp))
                .collect(Collectors.toList());
    }


    private boolean isTransactionInTheFuture(long metricTimestamp, long currentTimestamp) {
        //if the transaction is in time range
        return currentTimestamp - metricTimestamp < 0;
    }
}
