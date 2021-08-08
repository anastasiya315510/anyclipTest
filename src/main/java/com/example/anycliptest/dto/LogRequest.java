package com.example.anycliptest.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LogRequest {
    private String logFile;
    private Integer logLimit;
    private ABTestRequest abTest;
}
