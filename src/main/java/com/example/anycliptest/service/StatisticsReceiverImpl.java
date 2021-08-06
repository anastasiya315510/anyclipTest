/*
 author Anastasiya
 created on 03/08/2021
 */


package com.example.anycliptest.service;


import com.example.anycliptest.model.GeneralStatistics;
import org.springframework.stereotype.Service;

import static com.example.anycliptest.aspect.MyAspect.timeSetLong;
import static com.example.anycliptest.aspect.MyAspect.writingTimeSetLong;
import static com.example.anycliptest.aspect.MyAspect.requestCount;


@Service
public class StatisticsReceiverImpl implements StatisticsReceiver {
    @Override
    public GeneralStatistics getStatistics() {
        double averageProcessTime = timeSetLong.stream().mapToDouble(a -> a).average().orElse(0.0);
        double averageWritingTime= writingTimeSetLong.stream().mapToDouble(a -> a).average().orElse(0.0);

        return GeneralStatistics.builder().totalRequests(requestCount.get()).avgProcessTime(averageProcessTime).avgWriteTime(averageWritingTime).build();
    }
}
