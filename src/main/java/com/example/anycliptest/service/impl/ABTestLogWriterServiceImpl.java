package com.example.anycliptest.service.impl;


import com.example.anycliptest.dto.LogRequest;
import com.example.anycliptest.dto.LogResponse;
import com.example.anycliptest.dto.VariantRequest;
import com.example.anycliptest.service.ABTestLogWriterService;
import com.example.anycliptest.service.FileWriterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class ABTestLogWriterServiceImpl implements ABTestLogWriterService {
    private ConcurrentLinkedDeque<LogRequest> requests = new ConcurrentLinkedDeque<>();
    private Map<String, Integer> percentage = new ConcurrentHashMap<>();
    private Map<String, Range<Integer>> resMap = new HashMap<>();
    int i = 0;
    private long numOfLines;

    private final Lock lock = new ReentrantLock();

    private final AtomicLong numberOfLines = new AtomicLong(0);
    private final AtomicBoolean isFirstCall = new AtomicBoolean(true);

    @Autowired
    private FileWriterService fileWriterService;


    @Override
    public LogResponse writeToFile(LogRequest logRequest) {
        if (requests.isEmpty()) {
            requests.add(logRequest);
            setPercentageInFirstRequest(requests.getFirst());
        }
        requests.add(logRequest);
        var random = new Random().nextInt(100);
        String variant = resMap.entrySet().stream().filter(a -> a.getValue().contains(random)).map(Map.Entry::getKey).findFirst().get();
        String fileName = requests.getLast().getAbTest().getVariants().stream().filter(a -> a.getVariantName().contains(variant)).findFirst().map(VariantRequest::getLogFile).orElse(requests.getFirst().getLogFile());
        log.info("fileName: " + fileName);
        if (Files.exists(Path.of(fileName))) {
            try (Stream<String> lines = Files.lines(Path.of(fileName), Charset.defaultCharset())) {
                numOfLines = lines.count();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (numOfLines > requests.getFirst().getLogLimit()) {
                log.error("Count of lines more than expected. Expected: " + logRequest.getLogLimit() + " Current: " + numOfLines);
                return LogResponse.builder().build();
            }
        }
        writeToFile(fileName, variant);
        return LogResponse.builder().logFile(fileName).value(variant).build();

    }



    private void setPercentageInFirstRequest(LogRequest logRequest) {
        percentage = logRequest.getAbTest().getVariants().stream().collect(Collectors.toMap(VariantRequest::getVariantName, VariantRequest::getPercentage));


        percentage
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        percentage.entrySet().forEach(a -> resetValue(a));
    }


    private void resetValue(Map.Entry<String, Integer> aValue) {
        int min = i;
        i += aValue.getValue();
        int max = i;
        Range<Integer> between = Range.between(min, max);
        log.info("value: " + i + " " + between);
        String key = aValue.getKey();
        resMap.put(key, between);
        log.info("result: " + resMap);

    }


    public void writeToFile(String fileName, String variant) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(variant);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}