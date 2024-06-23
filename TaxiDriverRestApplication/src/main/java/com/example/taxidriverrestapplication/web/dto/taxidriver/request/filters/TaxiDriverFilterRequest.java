package com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class TaxiDriverFilterRequest {
    @Nullable
    Integer companyId;

    @Nullable
    Integer minAge;

    @Nullable
    Integer maxAge;
}
