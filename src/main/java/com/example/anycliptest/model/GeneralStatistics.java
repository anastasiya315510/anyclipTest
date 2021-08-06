/*
 author Anastasiya
 created on 05/08/2021
 */


package com.example.anycliptest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeneralStatistics {
    private double totalRequests;
    private double avgProcessTime;
    private double avgWriteTime;
}
