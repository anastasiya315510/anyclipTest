package com.example.anycliptest;

import com.example.anycliptest.dto.ABTestRequest;
import com.example.anycliptest.dto.LogRequest;
import com.example.anycliptest.dto.LogResponse;
import com.example.anycliptest.dto.VariantRequest;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = AnyclipTestApplication.class
)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AnyclipTestApplicationTests {

    private static final String LOG_WRITE_URL = "http://localhost:8080/api/v1/log";
    private static final RestTemplate restTemplate = new RestTemplate();



    @Nested
    @DisplayName("Write logs to default file, when AB Tests - disabled")
    class DefaultLogWriteTest {
        private static final String DEFAULT_FILENAME = "test.log";

        @BeforeEach
        @SneakyThrows
        public void removeTestFile() {
            var removed = Files.deleteIfExists(Path.of(DEFAULT_FILENAME));
            log.info("Test file [{}] was{} removed\n", DEFAULT_FILENAME, (removed ? "" : "not"));
        }

        @Test
        @DisplayName("should create log file with 1 line when writing a single log line")
        public void shouldCreateLogFileAndWriteASingleLogLine() {

            var maxRowsCount = 1;

            var response = executeLogRequest(getLogRequestForDefault(maxRowsCount));
            Assertions.assertEquals(DEFAULT_FILENAME, response.getLogFile());
            Assertions.assertEquals(maxRowsCount, getLogLinesCount(DEFAULT_FILENAME));
        }

        @Test
        @DisplayName("should create a log file and write exactly 130 lines, when executed 201 requests")
        public void shouldCreateALogFileAndWriteExactNumberOfLines() {

            var maxLines = 130;
            var requestCount = 201;

            var isShutdownCorrectly = executeConcurrentLogRequests(
                    getLogRequestForDefault(maxLines),
                    requestCount
            );

            Assertions.assertTrue(isShutdownCorrectly);
            Assertions.assertEquals(maxLines, getLogLinesCount(DEFAULT_FILENAME));

        }

        @Test
        @DisplayName("should not write logs, when such file exists and has exceeded rows count")
        @SneakyThrows
        public void shouldNotWriteLogsWhenExistsAndExceededRowsCount() {
            var maxRows = 50;

            try (var pw = new PrintWriter(new FileWriter(DEFAULT_FILENAME))) {
                Stream.generate(() -> "message")
                        .limit(50)
                        .forEach(pw::println);
            }

            Assertions.assertEquals(maxRows, getLogLinesCount(DEFAULT_FILENAME));

            var request = getLogRequestForDefault(maxRows);
            executeConcurrentLogRequests(request, maxRows);

            Assertions.assertEquals(maxRows, getLogLinesCount(DEFAULT_FILENAME));
        }



        private LogRequest getLogRequestForDefault(Integer logLimit) {
            return LogRequest.builder()
                    .abTest(ABTestRequest.builder().enabled(false).build())
                    .logFile(DEFAULT_FILENAME)
                    .logLimit(logLimit)
                    .build();
        }
    }

    @Nested
    @DisplayName("Write logs with AB Tests - enabled")
    class VariantsLogWriteTest {

        @Test
        public void abc() {

            var variants = List.of(
                    VariantRequest.builder().logFile("a.log").variantName("a").percentage(33).build(),
                    VariantRequest.builder().logFile("b.log").variantName("b").percentage(33).build(),
                    VariantRequest.builder().logFile("c.log").variantName("c").percentage(34).build()
            );

            var request = LogRequest.builder()
                    .logFile("abc.log")
                    .logLimit(200)
                    .abTest(ABTestRequest.builder()
                            .enabled(true)
                            .variants(variants)
                            .build())
                    .build();

            executeConcurrentLogRequests(request, 100);
        }

    }




    private LogResponse executeLogRequest(LogRequest request) {
        return restTemplate.postForObject(
                LOG_WRITE_URL,
                request,
                LogResponse.class
        );
    }

    @SneakyThrows
    private boolean executeConcurrentLogRequests(LogRequest request, Integer requestsCount) {
        var maxServerRequestCapacity = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(maxServerRequestCapacity);

        Runnable runnable = () -> executeLogRequest(request);
        var chunks = Lists.partition(Stream.generate(() -> runnable).limit(requestsCount).collect(Collectors.toList()), requestsCount);

        chunks.forEach(chunk -> {
            chunk.forEach(executorService::execute);
            sleep(500);
        });


        executorService.shutdown();
        return executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }


    @SneakyThrows
    private Long getLogLinesCount(String filename) {
        return Files.lines(Path.of(filename)).count();
    }

    @SneakyThrows
    private void sleep(Integer millis) {
        Thread.sleep(millis);
    }


}
