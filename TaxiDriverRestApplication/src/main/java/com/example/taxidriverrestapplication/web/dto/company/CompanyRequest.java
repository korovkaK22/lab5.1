package com.example.taxidriverrestapplication.web.dto.company;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyRequest {

    @NotBlank(message = "Name cannot be null or empty.")
    @Size(min = 3, max = 255, message = "The length of full name must be between 3 and 255 characters.")
    private String name;

    @NotBlank(message = "Country cannot be null or empty.")
    @Size(min = 3, max = 255, message = "The length of country name must be between 3 and 255 characters.")
    private String country;

    @NotNull(message = "Working cars amount cannot be null.")
    @Min(value = 0, message = "Car amount can't be less than zero")
    private Integer workingCarsAmount;
}
