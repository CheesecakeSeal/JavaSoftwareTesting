import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;

public class SampleCalcAppLoadTest {

    private static final int THREAD_COUNT = 100; // Number of concurrent threads
    private static final int REQUESTS_PER_THREAD = 50; // Number of requests each thread will send
    private static final String BASE_URL = "http://localhost:7000";

    public static void main(String[] args) {
        // Configure RESTAssured
        RestAssured.baseURI = BASE_URL;

        // Executor for managing threads
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        // List to hold future responses
        List<Future<LoadTestResult>> futures = new ArrayList<>();

        // Atomic counters for metrics
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger failedRequests = new AtomicInteger(0);
        AtomicInteger totalResponseTime = new AtomicInteger(0);

        // Track system metrics
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        System.out.println("Starting load test with " + THREAD_COUNT + " threads...");

        // Submit tasks for each thread
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(() -> runLoadTest(successfulRequests, failedRequests, totalResponseTime)));
        }

        // Monitor system metrics periodically
        ScheduledExecutorService metricsExecutor = Executors.newSingleThreadScheduledExecutor();
        metricsExecutor.scheduleAtFixedRate(() -> {
            double cpuLoad = getSystemCpuLoad(osBean);
            MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
            System.out.printf("CPU Load: %.2f%%, Heap Memory Used: %d MB%n",
                    cpuLoad * 100, heapMemoryUsage.getUsed() / (1024 * 1024));
        }, 0, 1, TimeUnit.SECONDS);

        // Wait for all threads to complete
        for (Future<LoadTestResult> future : futures) {
            try {
                future.get(); // Retrieve results (blocks until the thread completes)
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Shutdown executors
        executorService.shutdown();
        metricsExecutor.shutdown();

        // Calculate and print metrics
        int totalRequests = THREAD_COUNT * REQUESTS_PER_THREAD;
        int successCount = successfulRequests.get();
        int failureCount = failedRequests.get();
        int averageResponseTime = successCount > 0 ? totalResponseTime.get() / successCount : 0;

        System.out.println("Load Test Results:");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful Requests: " + successCount);
        System.out.println("Failed Requests: " + failureCount);
        System.out.println("Average Response Time: " + averageResponseTime + " ms");
    }

    private static LoadTestResult runLoadTest(AtomicInteger successfulRequests, AtomicInteger failedRequests, AtomicInteger totalResponseTime) {
        LoadTestResult result = new LoadTestResult();
        for (int i = 0; i < REQUESTS_PER_THREAD; i++) {
            long startTime = System.currentTimeMillis();
            try {
                // Perform HTTP POST request. Random Inputs are used to simulate diverse calculations
                double num1 = Math.random() * 100;
                double num2 = Math.random() * 100;
                char operator = randomOperator();

                given()
                        .contentType(ContentType.JSON)
                        .body("{\"num1\":" + num1 + ", \"num2\":" + num2 + ", \"operator\": \"" + operator + "\"}")
                        .when()
                        .post("/calculate")
                        .then()
                        .statusCode(200);

                // Success metrics
                successfulRequests.incrementAndGet();
            } catch (Exception e) {
                // Failure metrics
                failedRequests.incrementAndGet();
                result.failedRequests.add(e.getMessage());
            } finally {
                long responseTime = System.currentTimeMillis() - startTime;
                totalResponseTime.addAndGet((int) responseTime);
                result.responseTimes.add((int) responseTime);
            }
        }
        return result;
    }

    // For random calculations
    private static char randomOperator() {
        char[] operators = {'+', '-', '*', '/'};
        return operators[(int) (Math.random() * operators.length)];
    }

    private static double getSystemCpuLoad(OperatingSystemMXBean osBean) {
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getCpuLoad();
        }
        return -1; // Unsupported
    }

    // Helper class to store individual thread results
    static class LoadTestResult {
        List<Integer> responseTimes = new ArrayList<>();
        List<String> failedRequests = new ArrayList<>();
    }
}
