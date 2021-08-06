/*
 author Anastasiya
 created on 05/08/2021
 */


package com.example.anycliptest.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Request {
    private String logFile;
    private String logLimit;
    private ABTest abTest;
}
