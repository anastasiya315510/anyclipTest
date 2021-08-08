package com.example.anycliptest.service.impl;


import com.example.anycliptest.dto.LogRequest;
import com.example.anycliptest.dto.LogResponse;
import com.example.anycliptest.service.DefaultLogWriterService;
import com.example.anycliptest.service.FileWriterService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultLogWriterServiceImpl implements DefaultLogWriterService {

    private final Lock lock = new ReentrantLock();

    private final AtomicLong numberOfLines = new AtomicLong(0);
    private final AtomicBoolean isFirstCall = new AtomicBoolean(true);

    private final FileWriterService fileWriterService;

    @Override
    @SneakyThrows
    public LogResponse writeToFile(LogRequest logRequest, String message) {

        // on server restart with existing log file
        if (isFirstCall.get()) {
            lock.lock();
            if (isFirstCall.get()) {
                if (fileWriterService.fileExists(logRequest.getLogFile())) {
                    var logLinesCount = fileWriterService.getLinesCount(logRequest.getLogFile());
                    log.info("File [{}] exists and already has {} lines", logRequest.getLogFile(), logLinesCount);

                    numberOfLines.set(logLinesCount);
                }
            }
            isFirstCall.set(false);
            lock.unlock();
        }

        if (numberOfLines.getAndIncrement() >= logRequest.getLogLimit()) {
            log.warn("Number of lines exceeded max allowed: {}", logRequest.getLogLimit());
            return LogResponse.builder().build();
        }

        fileWriterService.writeToFile(logRequest.getLogFile(), message);

        return LogResponse.builder().logFile(logRequest.getLogFile()).value(message).build();
    }

}
