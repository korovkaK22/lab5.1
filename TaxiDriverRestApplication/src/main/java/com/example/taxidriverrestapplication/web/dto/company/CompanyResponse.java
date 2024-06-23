package com.example.taxidriverrestapplication.web.dto.company;

import com.example.taxidriverrestapplication.entity.Company;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.constraints.*;

@Getter
@Setter
@RequiredArgsConstructor
public class CompanyResponse {

    private final Integer id;

    private final String name;

    private final String country;

    private final int workingCarsAmount;

    public CompanyResponse(Company company) {

        this.id = company.getId();
        this.name = company.getName();
        this.country = company.getCountry();
        this.workingCarsAmount = company.getWorkingCarsAmount();
    }
}
