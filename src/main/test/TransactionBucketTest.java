import com.n26.pojo.Transaction;
import com.n26.statistics.TransactionBucket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class TransactionBucketTest {

    Transaction transaction;
    TransactionBucket transactionBucket;
    @Before
    public void setUp(){
        transactionBucket = new TransactionBucket();
    }
    @Test
    public void shouldCreateNewMetric(){
        transactionBucket.create(new Transaction(new BigDecimal(1.00), Timestamp.from(Instant.now())));
        Assert.assertEquals(new BigDecimal(1).setScale(2),transactionBucket.getMetric().getSum());
        Assert.assertEquals(new BigDecimal(1).setScale(2),transactionBucket.getMetric().getMax());
        Assert.assertEquals(new BigDecimal(1).setScale(2),transactionBucket.getMetric().getMin());
        Assert.assertEquals(1,transactionBucket.getMetric().getCount(),0);
        Assert.assertEquals(new BigDecimal(1).setScale(2),transactionBucket.getMetric().getAvg());

    }

    @Test
    public void shouldUpdateExistingMetric(){
        transactionBucket.create(new Transaction(new BigDecimal(1.00), Timestamp.from(Instant.now())));
        transactionBucket.update(new Transaction(new BigDecimal(2.00), Timestamp.from(Instant.now())));
        Assert.assertEquals(new BigDecimal(3).setScale(2),transactionBucket.getMetric().getSum());
        Assert.assertEquals(new BigDecimal(2).setScale(2),transactionBucket.getMetric().getMax());
        Assert.assertEquals(new BigDecimal(1).setScale(2),transactionBucket.getMetric().getMin());
        Assert.assertEquals(2,transactionBucket.getMetric().getCount(),0);
        Assert.assertEquals(new BigDecimal(1.5).setScale(2),transactionBucket.getMetric().getAvg());
    }

    @Test
    public void shouldResetMetric(){
        transactionBucket.create(new Transaction(new BigDecimal(1.00), Timestamp.from(Instant.now())));
        transactionBucket.update(new Transaction(new BigDecimal(2.00), Timestamp.from(Instant.now())));
        transactionBucket.reset();
        Assert.assertEquals(new BigDecimal(0).setScale(2),transactionBucket.getMetric().getSum());
        Assert.assertEquals(new BigDecimal(Long.MIN_VALUE).setScale(2),transactionBucket.getMetric().getMax());
        Assert.assertEquals(new BigDecimal(Long.MAX_VALUE).setScale(2),transactionBucket.getMetric().getMin());
        Assert.assertEquals(0,transactionBucket.getMetric().getCount(),0);
        Assert.assertEquals(new BigDecimal(0).setScale(2),transactionBucket.getMetric().getAvg());
        Assert.assertEquals(0L,transactionBucket.getTimeStamp(),0);
    }
}
