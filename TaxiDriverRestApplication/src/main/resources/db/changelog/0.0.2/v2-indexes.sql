--liquibase formatted sql

--changeset Andrii Sereda:v2-1 runOnChange:true
CREATE INDEX idx_taxi_driver_company_id ON taxi_drivers (company_id);
CREATE INDEX idx_taxi_driver_company_age ON taxi_drivers (company_id, age);