package com.example.taxidriverrestapplication.web.dto.taxidriver.response;

import com.example.taxidriverrestapplication.entity.Company;
import com.example.taxidriverrestapplication.entity.TaxiDriver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TaxiDriverResponse {
    private Integer id;
    private String name;
    private String surname;
    private Integer companyId;
    private int age;
    private Integer drivingExperience;
    private Long salary;
    private String cars;


    public TaxiDriverResponse(TaxiDriver taxiDriver) {
        this.id = taxiDriver.getId();
        this.name = taxiDriver.getName();
        this.surname = taxiDriver.getSurname();
        Company company = taxiDriver.getCompany();
        this.companyId = company== null? null: company.getId();
        this.drivingExperience = taxiDriver.getDrivingExperience();
        this.salary = taxiDriver.getSalary();
        this.cars = taxiDriver.getCars();
    }


}
