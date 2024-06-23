package com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TaxiDriverPaginationFilterRequest extends TaxiDriverFilterRequest{



    @NotNull(message = "Page cannot be null or empty.")
    Integer page;
    @NotNull(message = "Size cannot be null or empty.")
    Integer size;

}
