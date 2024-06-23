package com.example.taxidriverrestapplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;




@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "companies")
@Entity
@NoArgsConstructor
public class Company {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;

 // can't be duplicated in different companies
 @NotBlank(message = "Name cannot be null or empty.")
 @Column(name = "name", unique = true)
 private String name;

 @NotBlank(message = "Country cannot be null or empty.")
 @Column(name = "country")
 private String country;

 @Min(value = 0, message = "Car amount can't be less than zero")
 @Column(name = "working_cars_amount")
 private int workingCarsAmount;

}
