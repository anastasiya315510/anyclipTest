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
@Getter
public class Variant {
    private String variantName;
    private int percentage;
    private String logFile;

}
