import com.n26.Application;
import com.n26.exception.InFutureTransactionException;
import com.n26.exception.OldTransactionException;
import com.n26.pojo.Transaction;
import com.n26.statistics.Statistics;
import com.n26.statistics.TransactionBucket;
import com.n26.statistics.TransactionStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TransactionStatisticsTest {
    @Autowired
    private TransactionStatistics transactionStatistics;

    @Autowired
    private Statistics statistics;


    @Test(expected=OldTransactionException.class )
    public void shouldReturnOldTransactionException() throws OldTransactionException{
        Transaction transaction = new Transaction(new BigDecimal(12.5).setScale(2), Timestamp.from(Instant.ofEpochMilli(123)));
        transactionStatistics.addTransaction(transaction,Timestamp.from(Instant.now()).getTime());
    }

    @Test(expected= InFutureTransactionException.class )
    public void shouldReturnInfutureTransactionException() throws InFutureTransactionException{
        Transaction transaction = new Transaction(new BigDecimal(12.5).setScale(2), Timestamp.from(Instant.now().plusSeconds(600)));
        transactionStatistics.addTransaction(transaction,Timestamp.from(Instant.now()).getTime());
    }

    @Test
    public void testEmptyGettingAggregatorsWithInValidTime() {
        long time = Instant.now().toEpochMilli();
        Transaction transaction = new Transaction(new BigDecimal(12.5).setScale(2), Timestamp.from(Instant.ofEpochMilli(123)));
        try {
            transactionStatistics.addTransaction(transaction,time);
        } catch (OldTransactionException e) {}

        List<TransactionBucket> list = transactionStatistics.getValidTransactionsBuckets(time);

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void testConcurrentTransactions(){
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        long time = Instant.now().toEpochMilli();
        try{
            IntStream.range(0, 100).forEach(i-> {
                executor.execute(()->{
                    Transaction t = new Transaction(new BigDecimal(10 * i).setScale(2),
                            Timestamp.from(Instant.ofEpochMilli(time - (i + i * 100))) );
                    try {
                        Thread.sleep(1);
                        transactionStatistics.addTransaction(t,time);
                    } catch (Exception e) {}
                });

            });

        }finally{
            executor.shutdown();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        List<TransactionBucket> list = transactionStatistics.getValidTransactionsBuckets(time);

        assertNotNull(list);

        int sum = 0;
        BigDecimal max = new BigDecimal(1000);
        BigDecimal min = new BigDecimal(0.00);

        for (TransactionBucket bucket : list){
            sum += bucket.getMetric().getCount();
            max = bucket.getMetric().getMax().max(max);
            min = bucket.getMetric().getMin().min(min);
        }

        assertEquals(100, sum);
        assertEquals(new BigDecimal(1000), max);
        assertEquals(new BigDecimal(0.00).setScale(2), min);
    }


}
