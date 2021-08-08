package com.example.anycliptest.service.impl;


import com.example.anycliptest.dto.GeneralStatisticsResponse;
import com.example.anycliptest.service.StatisticsService;
import org.springframework.stereotype.Service;

import static com.example.anycliptest.aspect.ProfilingAspect.timeSetLong;
import static com.example.anycliptest.aspect.ProfilingAspect.writingTimeSetLong;
import static com.example.anycliptest.aspect.ProfilingAspect.requestCount;


@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Override
    public GeneralStatisticsResponse getStatistics() {
        double averageProcessTime = timeSetLong.stream().mapToDouble(a -> a).average().orElse(0.0);
        double averageWritingTime= writingTimeSetLong.stream().mapToDouble(a -> a).average().orElse(0.0);

        return GeneralStatisticsResponse.builder().totalRequests(requestCount.get()).avgProcessTime(averageProcessTime).avgWriteTime(averageWritingTime).build();
    }
}
