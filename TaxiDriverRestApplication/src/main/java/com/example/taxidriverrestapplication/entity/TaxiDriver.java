package com.example.taxidriverrestapplication.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@EqualsAndHashCode
@ToString
@Table(name = "taxi_drivers")
@NamedQuery(name = "TaxiDriver.findByCompanyAndAge",
        query = "SELECT td FROM TaxiDriver td WHERE " +
                "(:companyId IS NULL OR td.company.id = :companyId) AND " +
                "(:minAge IS NULL OR td.age >= :minAge) AND " +
                "(:maxAge IS NULL OR td.age <= :maxAge)")
public class TaxiDriver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name cannot be null or empty.")
    private String name;

    @NotBlank(message = "Surname cannot be null or empty.")
    private String surname;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Min(value = 18, message = "Age must be at least 18.")
    @Max(value = 100, message = "Age must be less than or equal to 100.")
    private int age;

    @Min(value = 0, message = "Driving experience must be non-negative.")
    @Max(value = 90, message = "Driving experience cannot exceed 90 years.")
    @Column(name = "driving_experience")
    private int drivingExperience;

    @Min(value = 0, message = "Salary must be non-negative.")
    @Max(value = 10000000, message = "Salary must be less than or equal to 10,000,000.")
    private long salary;

    @NotBlank(message = "Cars list cannot be null or empty.")
    private String cars;


}