package com.example.taxidriverrestapplication.web.controllers;

import com.example.taxidriverrestapplication.services.TaxiDriverService;
import com.example.taxidriverrestapplication.web.dto.UploadJsonEntitiesResponse;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters.TaxiDriverFilterRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters.TaxiDriverPaginationFilterRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.TaxiDriverRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.response.TaxiDriverFullResponse;
import com.example.taxidriverrestapplication.web.dto.taxidriver.response.TaxiDriverResponse;
import com.example.taxidriverrestapplication.web.dto.taxidriver.response.TaxiDriverShortResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/taxi-driver")
public class TaxiDriverController {

    private final TaxiDriverService taxiDriverService;

    @Autowired
    public TaxiDriverController(TaxiDriverService taxiDriverService) {
        this.taxiDriverService = taxiDriverService;
    }

    @PostMapping()
    public ResponseEntity<TaxiDriverFullResponse> saveTaxiDriver(@Valid @RequestBody TaxiDriverRequest taxiDriverRequest) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new TaxiDriverFullResponse(taxiDriverService.saveTaxiDriver(taxiDriverRequest)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaxiDriverFullResponse> getTaxiDriver(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new TaxiDriverFullResponse(taxiDriverService.getTaxiDriver(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaxiDriverResponse> updateTaxiDriver(@PathVariable Integer id,
                                                               @Valid @RequestBody TaxiDriverRequest taxiDriverRequest) {
       try {
           return ResponseEntity.ok(new TaxiDriverResponse(taxiDriverService.updateTaxiDriver(id, taxiDriverRequest)));
       } catch (IllegalArgumentException e) {
           return ResponseEntity.notFound().build();
       }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaxiDriver(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok().body(new TaxiDriverResponse(taxiDriverService.deleteTaxiDriver(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/_list")
    public ResponseEntity<?> getListOfTaxiDrivers(@Valid @RequestBody TaxiDriverPaginationFilterRequest request) {
        try {
            return ResponseEntity.ok(taxiDriverService.getListOfTaxiDrivers(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/_report")
    public ResponseEntity<Void> downloadTaxiDriverReport(TaxiDriverFilterRequest request, HttpServletResponse response) throws IOException {
        List<TaxiDriverShortResponse> drivers = taxiDriverService.getReportOfTaxiDrivers(request);
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"drivers.csv\"");

        try (CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(),
                CSVFormat.DEFAULT.withHeader("ID", "Name", "Surname", "CompanyId", "Age"))) {
            taxiDriverService.writeDriversIntoSCVFile(csvPrinter, drivers);
            csvPrinter.flush();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadTaxiDrivers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("File with drivers is empty");
            return ResponseEntity.badRequest().body("File with drivers is empty");
        }
        UploadJsonEntitiesResponse stats;
        try {
            stats = taxiDriverService.uploadTaxiDriversFromFile(file);
            log.info("File was processed, uploaded drivers: %d, invalid drivers: %d".formatted(stats.getSuccessful(), stats.getFailed()));
        } catch (Exception e) {
            log.warn("Error while uploading drivers from file");
            return ResponseEntity.badRequest().body("Error while uploading drivers from file: " + e.getMessage());
        }

        return ResponseEntity.ok(stats);

    }

}
