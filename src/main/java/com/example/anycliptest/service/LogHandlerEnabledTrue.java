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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LogHandlerEnabledTrue implements EnabledTrue {
private ConcurrentLinkedDeque<Request> requests = new ConcurrentLinkedDeque<>();
    private Map<String, Integer> percentage;
    HashMap<String, Range<Integer>> resMap = new HashMap<>();
    int i = 0;

    //TODO log file
    //TODO variant name

    @Override
    public Response putToFile(Request logRequest) {
        if(requests.isEmpty()) {
            requests.add(logRequest);
            setPercentageInFirstRequest(requests.getFirst());
        }
        requests.add(logRequest);
        var random =  new Random().nextInt(100);
        String fileName = resMap.entrySet().stream().filter(a -> a.getValue().contains(random)).map(Map.Entry::getKey).findFirst().get();
        resMap.entrySet().stream().filter(a -> a.getValue().contains(random)).map(Map.Entry::getKey).findFirst().get();
        log.info("fileName: "+ fileName);
        writeToFile(fileName, requests.getLast());
        return Response.builder().logFile(fileName).value(logRequest.toString()).build();

    }

    private void setPercentageInFirstRequest(Request logRequest) {
//.collect(Collectors.toMap(
//                Map.Entry::getKey,
//                entry -> entry.getValue().stream().map(Placement::getId).collect(Collectors.toList())));
        percentage = logRequest.getAbTest().getVariants().stream().collect(Collectors.toMap(Variant::getVariantName, Variant::getPercentage));


        percentage
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2)->e1, LinkedHashMap::new));

        percentage.entrySet().forEach(a -> resetValue(a));
    }


    private void   resetValue(Map.Entry<String, Integer> aValue) {
        int min = i;
        i += aValue.getValue();
        int max =  i ;
        Range<Integer> between = Range.between(min, max);
        log.info("value: "+ i +" " +between);
        String key = aValue.getKey();
        resMap.put(key, between);
        log.info("result: "+ resMap);

    }



    public void writeToFile(String fileName, Request request) {
        try(FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(request.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}