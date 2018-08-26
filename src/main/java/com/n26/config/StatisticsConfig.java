package com.n26.config;

import com.n26.statistics.TransactionStatistics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class StatisticsConfig {

    @Bean
    public TransactionStatistics transactionStatistics() {
        return new TransactionStatistics();
    }

}