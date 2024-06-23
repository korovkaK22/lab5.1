package com.example.taxidriverrestapplication.web.dto.taxidriver.response;

import com.example.taxidriverrestapplication.entity.TaxiDriver;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Data
public class TaxiDriverPaginationResponse {
    private final List<TaxiDriverShortResponse> taxiDrivers = new ArrayList<>();
    private int totalPages;

    public void addTaxiDrivers(List<TaxiDriver> taxiDriverList) {
        taxiDrivers.addAll(taxiDriverList.stream().map(TaxiDriverShortResponse::new).toList());
    }
}
