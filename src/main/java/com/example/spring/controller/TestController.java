package com.example.spring.controller;

import com.example.spring.entity.Test;
import com.example.spring.repository.TestRepository;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class TestController {

    @Autowired
    TestRepository testRepository;

    @Autowired
    Executor executorService;

    @Autowired
    @Qualifier("executorService2")
    Executor executorService2;

    @Autowired
    @Qualifier("executorServiceTest1")
    Executor executorServiceTest1;

    @Autowired
    @Qualifier("executorServiceTest2")
    Executor executorServiceTest2;

    private final Faker faker;

    public TestController() {
        this.faker = new Faker();
    }

    @PostMapping("/test")
    public ResponseEntity<String> test() {
        AtomicLong allTimes = new AtomicLong();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    Test test = Test.builder()
                            .name(faker.name().username())
                            .build();

                    log.info("Saving user thread: {}", Thread.currentThread().getId());
                    testRepository.save(test);

                    long endTime = System.currentTimeMillis();
                    log.info("Time taken: {}", (endTime - startTime));
                    allTimes.addAndGet((endTime - startTime));

                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage(), e);
                }
            }, executorService);
            futures.add(future);
        }
        // Wait for all tasks to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join(); // Wait for all to finish
        log.info("Total time taken for all operations: {}", allTimes.get());

        return ResponseEntity.ok("Hello World");
    }

    @PostMapping("/test2")
    public ResponseEntity<?> test2() {
        AtomicLong allTimes = new AtomicLong();

        for (int i = 0; i < 10000; i++) {
            CompletableFuture.runAsync(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    Test test = Test.builder()
                            .name(faker.name().username())
                            .build();

                    log.info("Saving user thread: {}", Thread.currentThread().getId());
                    testRepository.save(test);

                    long endTime = System.currentTimeMillis();
                    log.info("Time taken: {}", (endTime - startTime));
                    allTimes.addAndGet((endTime - startTime));

                } catch (Exception e) {
                    log.error("Error: {}", e.getMessage(), e);
                }
            }, executorService2);
        }

        // Log the total time taken
        long totalTimeTaken = allTimes.get();
        log.info("Total time taken for all operations: {}", totalTimeTaken);
        return ResponseEntity.ok("Hello World");
    }

    @PostMapping("/test-thread")
    public ResponseEntity<?> testThread() {
        List<CompletableFuture<Void>> allFutures = new ArrayList<>();
        // Define tasks
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("Task 1 is running in thread " + Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(1); // Simulate a task that takes taskId seconds

                // List to hold all CompletableFuture tasks
                List<CompletableFuture<Void>> futures1 = new ArrayList<>();

                // Create and submit 10 tasks
                for (int i = 1; i <= 6; i++) {
                    final int taskId = i;
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            System.out.println("Task 1." + taskId + " is running in thread "  + Thread.currentThread().getName());
                            TimeUnit.SECONDS.sleep(1); // Simulate a task that takes taskId seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, executorServiceTest2);
                    futures1.add(future);
                }

                // Combine all tasks and wait for all of them to complete
                CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures1.toArray(new CompletableFuture[0]));

                // Wait for all tasks to complete
                allTasks.join();

                System.out.println("All tasks 1 are completed in thread "  + Thread.currentThread().getName());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, executorServiceTest1);

        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            log.info("available processor:: {}", Runtime.getRuntime().availableProcessors());
            try {
                  System.out.println("Task 2 is running in thread "  + Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(1); // Simulate a task that takes taskId seconds

                // List to hold all CompletableFuture tasks
                List<CompletableFuture<Void>> futures1 = new ArrayList<>();

                // Create and submit 10 tasks
                for (int i = 1; i <= 20; i++) {
                    final int taskId = i;
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            System.out.println("Task 2." + taskId + " is running in thread "  + Thread.currentThread().getName());
                            TimeUnit.SECONDS.sleep(1); // Simulate a task that takes taskId seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, executorServiceTest2);
                    futures1.add(future);
                }

                // Combine all tasks and wait for all of them to complete
                CompletableFuture<Void> allTasks = CompletableFuture.allOf(futures1.toArray(new CompletableFuture[0]));

                // Wait for all tasks to complete
                allTasks.join();

                System.out.println("All tasks 2 are completed in thread "  + Thread.currentThread().getName());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, executorServiceTest1);
        // Combine tasks and wait for all of them to complete
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2);

        // Wait for all tasks to complete
        allTasks.join();

        return null;
    }

}