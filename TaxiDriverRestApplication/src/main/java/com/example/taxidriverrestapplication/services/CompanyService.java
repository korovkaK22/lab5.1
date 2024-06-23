package com.example.taxidriverrestapplication.services;

import com.example.taxidriverrestapplication.entity.Company;
import com.example.taxidriverrestapplication.repositories.CompanyRepository;
import com.example.taxidriverrestapplication.web.dto.company.CompanyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company addCompany(CompanyRequest company) {
        Company newCompany = new Company();
        initCompanyFields(company, newCompany);
        Company save = companyRepository.save(newCompany);
        log.debug("Company with name %s added".formatted(company.getName()));
        return save;
    }

    public Optional<Company> getCompany(Integer id) {
        return companyRepository.findById(id);
    }



    public Company putCompany(Integer id, CompanyRequest companyRequest) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("Company with id " + id + " not found");
        }
        Company companyFromDb = companyOpt.get();
        Optional<Company> byName = companyRepository.findByName(companyRequest.getName());
        if (byName.isPresent() && !byName.get().equals(companyFromDb)) {
            throw new IllegalArgumentException("Company with name " + companyRequest.getName() + " already exists");
        }

        companyFromDb.setId(id);
        initCompanyFields(companyRequest, companyFromDb);
        companyRepository.save(companyFromDb);
        log.debug("Company with id %d updated".formatted(id));
        return companyFromDb;
    }

    public Company deleteCompany(Integer id) {
        Optional<Company> companyOpt = companyRepository.findById(id);

        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("Company with id " + id + " not found");
        }
        Company company = companyOpt.get();
        companyRepository.delete(company);
        log.debug("Company with id %d deleted".formatted(id));
        return company;
    }


    /**
     * Initializes company fields from companyRequest
     * @param companyRequest - request with company fields
     * @param company - company to initialize
     */
    private void initCompanyFields(CompanyRequest companyRequest, Company company) {
        company.setName(companyRequest.getName());
        company.setCountry(companyRequest.getCountry());
        company.setWorkingCarsAmount(companyRequest.getWorkingCarsAmount());
    }
}
