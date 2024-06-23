package com.example.taxidriverrestapplication;

import com.example.taxidriverrestapplication.entity.Company;
import com.example.taxidriverrestapplication.repositories.CompanyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class CompanyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyRepository companyRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    public void cleanUp() {
        companyRepository.deleteAll();
    }

    @Nested
    class CompanyRetrievalTests {
        @Test
        public void getAllCompanies_WhenCompaniesExist_ReturnsNonEmptyList() throws Exception {
            List<Company> companies = List.of(new Company(1, "Test Company", "Test Country", 100));
            Mockito.when(companyRepository.findAll()).thenReturn(companies);

            mockMvc.perform(get("/api/company"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Test Company"))
                    .andDo(print());
        }
    }

    @Nested
    class CompanyCreationTests {
        @Test
        public void createCompany_WithValidRequest_ReturnsCreated() throws Exception {
            Company company = new Company(2, "New Company", "New Country", 50);
            Mockito.when(companyRepository.save(Mockito.any(Company.class))).thenReturn(company);

            mockMvc.perform(post("/api/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new Company(0, "New Company", "New Country", 50))))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("New Company"))
                    .andDo(print());
        }

        @Test
        public void createCompany_WithInvalidRequest_ReturnsBadRequest() throws Exception {
            mockMvc.perform(post("/api/company")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @Nested
    class CompanyUpdateTests {
        @Test
        public void updateCompany_WhenNotFound_ReturnsNotFound() throws Exception {
            Mockito.when(companyRepository.findById(1)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/company/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new Company(1, "Updated Company", "Updated Country", 75))))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    class CompanyDeletionTests {
        @Test
        public void deleteCompany_WhenExists_ReturnsOk() throws Exception {
            Company company = new Company(1, "Delete Company", "Delete Country", 50);
            Mockito.when(companyRepository.findById(1)).thenReturn(Optional.of(company));
            Mockito.doNothing().when(companyRepository).delete(company);

            mockMvc.perform(delete("/api/company/1"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        public void deleteCompany_WhenNotFound_ReturnsNotFound() throws Exception {
            Mockito.when(companyRepository.findById(1)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/api/company/1"))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }
}
