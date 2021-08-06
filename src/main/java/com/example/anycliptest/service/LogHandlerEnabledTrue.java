/*
 author Anastasiya
 created on 05/08/2021
 */


package com.example.anycliptest.service;


import com.example.anycliptest.model.Request;
import com.example.anycliptest.model.Response;
import com.example.anycliptest.model.Variant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class LogHandlerEnabledTrue implements EnabledTrue {
    private ConcurrentLinkedDeque<Request> requests = new ConcurrentLinkedDeque<>();
    private Map<String, Integer> percentage;
    private HashMap<String, Range<Integer>> resMap = new HashMap<>();
    int i = 0;
    private long numOfLines;


    //TODO statistics
    //TODO tests


    @Override
    public Response putToFile(Request logRequest) {
        if (requests.isEmpty()) {
            requests.add(logRequest);
            setPercentageInFirstRequest(requests.getFirst());
        }
        requests.add(logRequest);
        var random = new Random().nextInt(100);
        String variant = resMap.entrySet().stream().filter(a -> a.getValue().contains(random)).map(Map.Entry::getKey).findFirst().get();
        String fileName = requests.getLast().getAbTest().getVariants().stream().filter(a -> a.getVariantName().contains(variant)).findFirst().map(Variant::getLogFile).orElse(requests.getFirst().getLogFile());
        log.info("fileName: " + fileName);
        if (Files.exists(Path.of(fileName))) {
            try (Stream<String> lines = Files.lines(Path.of(fileName), Charset.defaultCharset())) {
                numOfLines = lines.count();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (numOfLines > Integer.parseInt(requests.getFirst().getLogLimit())) {
                log.error("Count of lines more than expected. Expected: " + logRequest.getLogLimit() + " Current: " + numOfLines);
                return Response.builder().build();
            }
        }
        writeToFile(fileName, variant);
        return Response.builder().logFile(fileName).value(variant).build();

    }


    private void setPercentageInFirstRequest(Request logRequest) {
        percentage = logRequest.getAbTest().getVariants().stream().collect(Collectors.toMap(Variant::getVariantName, Variant::getPercentage));


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