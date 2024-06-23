--liquibase formatted sql

--changeset Andrii Sereda:v1-1 runOnChange:true
CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    working_cars_amount INT CHECK (working_cars_amount >= 0)
);

CREATE TABLE taxi_drivers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    company_id INTEGER NOT NULL,
    age INT CHECK (age >= 18 AND age <= 100),
    driving_experience INT CHECK (driving_experience >= 0 AND driving_experience <= 90),
    salary BIGINT CHECK (salary >= 0 AND salary <= 10000000),
    cars VARCHAR(255) NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL
);