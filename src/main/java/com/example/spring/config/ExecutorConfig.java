package com.example.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

@Configuration
public class ExecutorConfig {

    @Bean
    public Executor executorService() {
        return Executors.newFixedThreadPool(10000);
    }

    @Bean(name = "executorService2")
    public Executor executorService2() {
        return new ForkJoinPool();
    }

    @Bean(name = "executorServiceTest1")
    public Executor executorServiceTest1() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean(name = "executorServiceTest2")
    public Executor executorServiceTest2() {
        return Executors.newFixedThreadPool(10);
    }

}
