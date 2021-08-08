package com.example.anycliptest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralStatisticsResponse {
    private double totalRequests;
    private double avgProcessTime;
    private double avgWriteTime;
}
