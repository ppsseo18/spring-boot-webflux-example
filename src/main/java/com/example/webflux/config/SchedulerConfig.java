package com.example.webflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@Configuration
public class SchedulerConfig {

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private Integer maxConnectionPool;

    @Bean
    public Scheduler getJdbcScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(maxConnectionPool));
    }

}
