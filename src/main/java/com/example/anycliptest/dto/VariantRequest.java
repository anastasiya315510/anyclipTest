package com.example.anycliptest.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class VariantRequest {
    private String variantName;
    private Integer percentage;
    private String logFile;

}
