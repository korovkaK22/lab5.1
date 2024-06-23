package com.example.taxidriverrestapplication.services;

import com.example.taxidriverrestapplication.entity.Company;
import com.example.taxidriverrestapplication.entity.TaxiDriver;
import com.example.taxidriverrestapplication.repositories.TaxiDriverRepository;
import com.example.taxidriverrestapplication.web.dto.UploadJsonEntitiesResponse;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters.TaxiDriverFilterRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters.TaxiDriverPaginationFilterRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.response.TaxiDriverPaginationResponse;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.TaxiDriverRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.response.TaxiDriverShortResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaxiDriverService {
    private final TaxiDriverRepository taxiDriverRepository;
    private final CompanyService companyService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;



    public TaxiDriver saveTaxiDriver(TaxiDriverRequest taxiDriverRequest) throws IllegalArgumentException {
        TaxiDriver taxiDriver = new TaxiDriver();
        initFields(taxiDriverRequest, taxiDriver);
        TaxiDriver save = taxiDriverRepository.save(taxiDriver);
        log.debug("Taxi driver with name %s %s added".formatted(taxiDriver.getName(), taxiDriver.getSurname()));
        return save;
    }

    public TaxiDriver getTaxiDriver(Integer id) {
        Optional<TaxiDriver> taxiDriverOpt = taxiDriverRepository.findById(id);
        if (taxiDriverOpt.isEmpty()) {
            throw new IllegalArgumentException("Taxi driver with id %d not found".formatted(id));
        }
        return taxiDriverOpt.get();
    }

    public TaxiDriver updateTaxiDriver(Integer id, TaxiDriverRequest taxiDriverRequest) {
        Optional<TaxiDriver> taxiDriverOpt = taxiDriverRepository.findById(id);
        if (taxiDriverOpt.isEmpty()) {
            throw new IllegalArgumentException("Taxi driver with id %d not found".formatted(id));
        }
        TaxiDriver taxiDriverFromDb = taxiDriverOpt.get();
        initFields(taxiDriverRequest, taxiDriverFromDb);
        TaxiDriver save = taxiDriverRepository.save(taxiDriverFromDb);
        log.debug("Taxi driver with name %s %s updated".formatted(taxiDriverFromDb.getName(), taxiDriverFromDb.getSurname()));
        return save;
    }


    public TaxiDriver deleteTaxiDriver(Integer id) {
        Optional<TaxiDriver> taxiDriverOpt = taxiDriverRepository.findById(id);
        if (taxiDriverOpt.isEmpty()) {
            throw new IllegalArgumentException("Taxi driver with id %d not found".formatted(id));
        }
        taxiDriverRepository.deleteById(id);
        log.debug("Taxi driver with id %d deleted".formatted(id));
        return taxiDriverOpt.get();
    }

    public TaxiDriverPaginationResponse getListOfTaxiDrivers(TaxiDriverPaginationFilterRequest request) {
        Page<TaxiDriver> page = taxiDriverRepository.findByCompanyAndAge(
                request.getCompanyId(),
                request.getMinAge(),
                request.getMaxAge(),
                PageRequest.of(request.getPage() - 1, request.getSize())
        );
        int maxPage = page.getTotalPages();
        int neededPage = request.getPage();

        if (maxPage < neededPage && maxPage != 0) {
            throw new IllegalArgumentException("Page %d not found, max page for this request is %d".formatted(neededPage, maxPage));
        }

        TaxiDriverPaginationResponse response = new TaxiDriverPaginationResponse();
        response.addTaxiDrivers(page.getContent());
        response.setTotalPages(page.getTotalPages());
        log.debug("Taxi drivers list returned for page %d".formatted(neededPage));
        return response;
    }

    public void writeDriversIntoSCVFile(CSVPrinter csvPrinter, List<TaxiDriverShortResponse> drivers) throws IOException {
        for (TaxiDriverShortResponse driver : drivers) {
            csvPrinter.printRecord(
                    driver.getId(),
                    driver.getName(),
                    driver.getSurname(),
                    driver.getCompanyId(),
                    driver.getAge()
            );
        }
    }

    public List<TaxiDriverShortResponse> getReportOfTaxiDrivers(TaxiDriverFilterRequest request) {
        return taxiDriverRepository.findByCompanyAndAge(
                request.getCompanyId(),
                request.getMinAge(),
                request.getMaxAge()
        ).stream().map(TaxiDriverShortResponse::new).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public UploadJsonEntitiesResponse uploadTaxiDriversFromFile(MultipartFile file) throws IOException {
        JsonParser jsonParser = objectMapper.getFactory().createParser(file.getInputStream());
        int validDrivers = 0;
        int invalidDrivers = 0;

        if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
            while (jsonParser.nextToken() == JsonToken.START_OBJECT) {

                TaxiDriverRequest driver = objectMapper.readValue(jsonParser, TaxiDriverRequest.class);
                boolean isSaved = false;
                try {
                    isSaved = validAndSaveDriver(driver);
                } catch (Exception e) {
                    log.debug("Error while saving driver %s".formatted(driver), e);
                }

                if (isSaved) {
                    validDrivers++;
                } else {
                    invalidDrivers++;
                }
            }
        }
        jsonParser.close();
        return new UploadJsonEntitiesResponse(validDrivers, invalidDrivers);

    }


    private boolean validAndSaveDriver(TaxiDriverRequest driver) {
        Errors errors = new BeanPropertyBindingResult(driver, "taxiDriverRequest");
        validator.validate(driver, errors);
        if (driver.getName().equals("Patsy")) {
            System.out.println();
        }

        if (!errors.hasErrors()) {
            saveTaxiDriver(driver);
            log.debug("Taxi driver with name %s %s added".formatted(driver.getName(), driver.getSurname()));
            return true;
        } else {
            log.debug("Taxi driver with name %s %s not added. Errors: %s".formatted(driver.getName(),
                    driver.getSurname(), errors.getAllErrors()));
            return false;
        }
    }


    /**
     * Initialize fields of TaxiDriver entity from TaxiDriverRequest
     *
     * @param taxiDriverRequest TaxiDriverRequest
     * @param taxiDriver        TaxiDriver
     */
    private void initFields(TaxiDriverRequest taxiDriverRequest, TaxiDriver taxiDriver) {
        taxiDriver.setName(taxiDriverRequest.getName());
        taxiDriver.setSurname(taxiDriverRequest.getSurname());
        Integer companyId = taxiDriverRequest.getCompanyId();
        if (companyId != null) {
            Optional<Company> companyOpt = companyService.getCompany(companyId);
            if (companyOpt.isEmpty()) {
                throw new IllegalArgumentException("Company with id %d not found".formatted(companyId));
            }
            taxiDriver.setCompany(companyOpt.get());
        }
        taxiDriver.setAge(taxiDriverRequest.getAge());
        taxiDriver.setDrivingExperience(taxiDriverRequest.getDrivingExperience());
        taxiDriver.setSalary(taxiDriverRequest.getSalary());
        taxiDriver.setCars(taxiDriverRequest.getCars());
    }


}
