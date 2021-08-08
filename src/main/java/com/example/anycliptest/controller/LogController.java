package com.example.anycliptest.controller;


import com.example.anycliptest.dto.GeneralStatisticsResponse;
import com.example.anycliptest.dto.LogRequest;
import com.example.anycliptest.dto.LogResponse;
import com.example.anycliptest.dto.VariantRequest;
import com.example.anycliptest.service.DefaultLogWriterService;
import com.example.anycliptest.service.ABTestLogWriterService;
import com.example.anycliptest.service.StatisticsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RestController
@Slf4j
public class LogController {

    private final ABTestLogWriterService enableTrueHandler;
    private final DefaultLogWriterService defaultLogHandler;
    private final StatisticsService statistics;


    @PostMapping("/api/v1/log")
    public LogResponse putToFile(@RequestBody LogRequest logRequest) {

        if (!logRequest.getAbTest().isEnabled()) {
            log.info("  enabled is false");
            String message = "NA";
            return defaultLogHandler.writeToFile(logRequest, message);
        } else if (!checkVariants(logRequest)) {
            log.error("sum of percent is not 100");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        } else if (isVariantsMoreThen5(logRequest)) {
            log.error("variants should be up to 5");
            return LogResponse.builder().build();
        } else {
            return enableTrueHandler.writeToFile(logRequest);
        }
    }

    private boolean isVariantsMoreThen5(LogRequest logRequest) {
        return logRequest.getAbTest().getVariants().size() > 5;
    }

    private boolean checkVariants(LogRequest logRequest) {
        return logRequest.getAbTest().getVariants().stream().map(VariantRequest::getPercentage).reduce(0, Integer::sum) == 100;
    }

    @GetMapping("/api/v1/log/stats")
    public GeneralStatisticsResponse getStatistics() {
        return statistics.getStatistics();
    }

}
