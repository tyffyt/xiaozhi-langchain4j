package com.it.java.ai.langchain4j;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * 运行策略耗时
 */
public class ParallelProcessingDemo {
    public static void main(String[] args) throws Exception {
        // 模拟大量数据
        List<Integer> data = IntStream.range(0, 1000000)
                                      .boxed()
                                      .collect(Collectors.toList());

        // 方式1：使用线程池
        long start1 = System.currentTimeMillis();
        int result1 = processWithThreadPool(data);
        long end1 = System.currentTimeMillis();
        System.out.println("线程池处理耗时: " + (end1 - start1) + "ms, 结果: " + result1);

        // 方式2：使用并行流
        long start2 = System.currentTimeMillis();
        int result2 = data.parallelStream()
                          .filter(n -> n % 2 == 0)
                          .mapToInt(n -> n * 2)
                          .sum();
        long end2 = System.currentTimeMillis();
        System.out.println("并行流处理耗时: " + (end2 - start2) + "ms, 结果: " + result2);

        // 方式3：使用 CompletableFuture
        long start3 = System.currentTimeMillis();
        int result3 = processWithCompletableFuture(data);
        long end3 = System.currentTimeMillis();
        System.out.println("CompletableFuture处理耗时: " + (end3 - start3) + "ms, 结果: " + result3);

        // 自定义线程池
        ThreadPoolExecutor good = new ThreadPoolExecutor(
                4,                           // 核心线程数
                8,                                      // 最大线程数
                60L, TimeUnit.SECONDS,                  // 空闲线程存活时间
                new LinkedBlockingQueue<>(100), // 有界队列
                new ThreadFactory() {                   // 自定义线程工厂
                    private int count = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("MyPool-" + count++);
                        thread.setUncaughtExceptionHandler((t, e) -> {
                            System.err.println("线程" + t.getName() + "异常: " + e.getMessage());
                        });
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );

    }

    // 使用线程池处理
    private static int processWithThreadPool(List<Integer> data) throws Exception {
        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        int chunkSize = data.size() / threadCount;

        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int start = i * chunkSize;
            final int end = (i == threadCount - 1) ? data.size() : (i + 1) * chunkSize;

            Future<Integer> future = executor.submit(() -> {
                int sum = 0;
                for (int j = start; j < end; j++) {
                    int n = data.get(j);
                    if (n % 2 == 0) {
                        sum += n * 2;
                    }
                }
                return sum;
            });
            futures.add(future);
        }

        int totalSum = 0;
        for (Future<Integer> future : futures) {
            totalSum += future.get();
        }

        executor.shutdown();
        return totalSum;
    }

    // 使用 CompletableFuture 处理
    private static int processWithCompletableFuture(List<Integer> data) throws Exception {
        int threadCount = Runtime.getRuntime().availableProcessors();
        int chunkSize = data.size() / threadCount;

        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int start = i * chunkSize;
            final int end = (i == threadCount - 1) ? data.size() : (i + 1) * chunkSize;

            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                int sum = 0;
                for (int j = start; j < end; j++) {
                    int n = data.get(j);
                    if (n % 2 == 0) {
                        sum += n * 2;
                    }
                }
                return sum;
            });
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        allOf.get();

        return futures.stream()
                      .mapToInt(f -> {
                          try {
                              return f.get();
                          } catch (Exception e) {
                              return 0;
                          }
                      })
                      .sum();
    }
}
