package com.n26;

import com.n26.pojo.Metric;
import com.n26.statistics.Statistics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StatisticsTest {
    private Statistics stat;

   // @Before
   // public void setup(){
       //  stat = Statistics.getStatisticsInstance();
  //  }
    @Test
    public void shouldCreateInstance(){
      //  Statistics stat = Statistics.getStatisticsInstance();
       // stat.resetStatistics();
       // Statistics stat = new Statistics(new Metric(), new ReentrantReadWriteLock());
    }

    @Test
    public void shouldAssertUpdates(){
      //  stat.update(new BigDecimal(10.22));
      //  stat.update(new BigDecimal(10.223));
       // stat.update(new BigDecimal(10.2223456));
       // Assert.assertEquals(3,stat.getCount(),0);
       // Assert.assertEquals(new BigDecimal(30.66).setScale(2,BigDecimal.ROUND_HALF_UP),stat.getSum());
       // Assert.assertEquals(new BigDecimal(10.22).setScale(2,BigDecimal.ROUND_HALF_UP),stat.getAvg());
    }
}
