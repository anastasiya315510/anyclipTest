package com.example.anycliptest.service;


import com.example.anycliptest.dto.LogRequest;
import com.example.anycliptest.dto.LogResponse;

public interface ABTestLogWriterService {
    LogResponse writeToFile(LogRequest logRequest);
}
