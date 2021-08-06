/*
 author Anastasiya
 created on 05/08/2021
 */


package com.example.anycliptest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ABTest {
    private boolean enabled;
    private ArrayList<Variant> variants;
}
