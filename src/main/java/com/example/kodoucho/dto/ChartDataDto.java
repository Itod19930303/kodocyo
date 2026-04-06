package com.example.kodoucho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ChartDataDto {
    private List<String> labels;
    private List<Integer> data;
}
