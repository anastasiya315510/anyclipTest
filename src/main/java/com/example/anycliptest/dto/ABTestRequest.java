package com.example.anycliptest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ABTestRequest {
    private boolean enabled;
    private List<VariantRequest> variants;
}
