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
public class Response {
    private String logFile;
    private String value;
}
