package com.example.taxidriverrestapplication.web.controllers;

import com.example.taxidriverrestapplication.services.CompanyService;
import com.example.taxidriverrestapplication.services.EmailCompanySenderService;
import com.example.taxidriverrestapplication.web.dto.company.CompanyRequest;
import com.example.taxidriverrestapplication.web.dto.company.CompanyResponse;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;
    private final EmailCompanySenderService emailCompanySenderService;

    @Value("${spring.mail.receivers}")
    private String receiversString;
    private List<String> receivers;

    @PostConstruct
    public void init() {
        receivers = Arrays.stream(receiversString.split(",")).toList();
    }

    @GetMapping()
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies().stream().map(CompanyResponse::new).toList());
    }

    @PostMapping()
    public ResponseEntity<CompanyResponse> addCompany(@RequestBody @Valid CompanyRequest companyRequest) {
        CompanyResponse savedCompany = new CompanyResponse(companyService.addCompany(companyRequest));
        emailCompanySenderService.createCompanyEmail(companyRequest.getName(), receivers);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> putCompany(@PathVariable Integer id,
                                                      @RequestBody @Valid CompanyRequest companyRequest) {
        try {
            String oldName;
            if (companyService.getCompany(id).isPresent()){
                oldName = companyService.getCompany(id).get().getName();
            } else {
                return ResponseEntity.notFound().build();
            }

            CompanyResponse updatedCompany = new CompanyResponse(companyService.putCompany(id, companyRequest));
            emailCompanySenderService.updateCompanyEmail(oldName, companyRequest.getName(), receivers);
            return ResponseEntity.ok(updatedCompany);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Integer id) {
        try {
            CompanyResponse deletedCompany = new CompanyResponse(companyService.deleteCompany(id));
            emailCompanySenderService.deleteCompanyEmail(deletedCompany.getName(), receivers);
            return ResponseEntity.ok().body(deletedCompany);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
