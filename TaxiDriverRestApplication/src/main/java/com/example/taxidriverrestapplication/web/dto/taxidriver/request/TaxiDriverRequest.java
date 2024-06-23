package com.example.taxidriverrestapplication.web.dto.taxidriver.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiDriverRequest {

    @NotBlank(message = "Name cannot be null or empty.")
    @Size(min = 3, max = 255, message = "The length of name must be between 3 and 255 characters.")
    private String name;

    @NotBlank(message = "Surname cannot be null or empty.")
    @Size(min = 3, max = 255, message = "The length of surname must be between 3 and 255 characters.")
    private String surname;

    @Nullable
    private Integer companyId;

    @NotNull(message = "Driving experience cannot be null")
    @Min(value = 18, message = "Age must be at least 18.")
    @Max(value = 100, message = "Age must be less than or equal to 100.")
    private Integer age;

    @NotNull(message = "Driving experience cannot be null")
    @Min(value = 0, message = "Driving experience must be non-negative.")
    @Max(value = 90, message = "Driving experience cannot exceed 90 years.")
    private Integer drivingExperience;

    @NotNull(message = "Salary cannot be null")
    @Min(value = 0, message = "Salary can't be less than zero")
    private Long salary;

    @Nullable
    @Pattern(regexp = "^([\\p{L}0-9\\s]+)(,\\s*[\\p{L}0-9\\s]+)*$", message = "Cars must be a list separated by commas or null.")
    private String cars;

}
