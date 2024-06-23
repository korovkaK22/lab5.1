package com.example.taxidriverrestapplication;

import com.example.taxidriverrestapplication.repositories.TaxiDriverRepository;
import com.example.taxidriverrestapplication.entity.TaxiDriver;
import com.example.taxidriverrestapplication.services.TaxiDriverService;
import com.example.taxidriverrestapplication.web.dto.UploadJsonEntitiesResponse;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.TaxiDriverRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters.TaxiDriverFilterRequest;
import com.example.taxidriverrestapplication.web.dto.taxidriver.request.filters.TaxiDriverPaginationFilterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TaxiDriverControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaxiDriverRepository taxiDriverRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @AfterEach
    public void afterEach() {
        taxiDriverRepository.deleteAll();
    }

    @Nested
    class SavingTaxiDriverTest {

        @Test
        public void saveTaxiDriver_ValidRequest_ReturnsCreated() throws Exception {
            TaxiDriverRequest taxiDriverRequest = new TaxiDriverRequest(
                    "John", "Doe", 1, 35, 5, 30000L, "Toyota Corolla");
            TaxiDriver savedTaxiDriver = new TaxiDriver(
                    1, "John", "Doe", null, 35, 5, 30000L, "Toyota Corolla");

            Mockito.when(taxiDriverRepository.save(any(TaxiDriver.class))).thenReturn(savedTaxiDriver);

            mockMvc.perform(post("/api/taxi-driver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taxiDriverRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("John"))
                    .andDo(print());
        }

        @Test
        public void saveTaxiDriver_InvalidRequest_ReturnsBadRequest() throws Exception {
            TaxiDriverRequest taxiDriverRequest = new TaxiDriverRequest(
                    null, null, 1, 35, 5, 30000L, "Toyota Corolla");

            mockMvc.perform(post("/api/taxi-driver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taxiDriverRequest)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        public void saveTaxiDriver_InvalidCompany_ReturnsBadRequest() throws Exception {
            TaxiDriverRequest taxiDriverRequest = new TaxiDriverRequest(
                    "John", "Doe", 0, 35, 5, 30000L, "Toyota Corolla");

            mockMvc.perform(post("/api/taxi-driver")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(taxiDriverRequest)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @Nested
    class GettingTaxiDriverTest {
        @Test
        public void getTaxiDriver_ValidId_ReturnsOk() throws Exception {
            Optional<TaxiDriver> taxiDriver = Optional.of(getTaxiDriver());
            Mockito.when(taxiDriverRepository.findById(1)).thenReturn(taxiDriver);

            mockMvc.perform(get("/api/taxi-driver/{id}", 1))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John"))
                    .andDo(print());
        }

        @Test
        public void getTaxiDriver_InvalidId_ReturnsNotFound() throws Exception {
            Mockito.when(taxiDriverRepository.findById(1)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/taxi-driver/{id}", 1))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    class UpdatingTaxiDriverTest {
        @Test
        public void updateTaxiDriver_NotFound_ReturnsNotFound() throws Exception {
            Mockito.when(taxiDriverRepository.findById(1)).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/taxi-driver/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new TaxiDriverRequest(
                                    "Updated", "Doe", 1, 36, 6, 35000L, "Nissan Altima"))))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }

        @Test
        public void updateTaxiDriver_ValidId_ReturnsOk() throws Exception {
            Mockito.when(taxiDriverRepository.findById(1)).thenReturn(Optional.of(getTaxiDriver()));
            Mockito.when(taxiDriverRepository.save(any(TaxiDriver.class)))
                    .thenAnswer((Answer<TaxiDriver>) invocation -> {
                        Object[] args = invocation.getArguments();
                        return (TaxiDriver) args[0];
                    });

            mockMvc.perform(put("/api/taxi-driver/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new TaxiDriverRequest(
                                    "Updated", "Doe", 1, 36, 6, 35000L, "Nissan Altima"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated"))
                    .andDo(print());
        }
    }

    @Nested
    class DeletingTaxiDriverTest {
        @Test
        public void deleteTaxiDriver_ValidId_ReturnsOk() throws Exception {
            Mockito.when(taxiDriverRepository.existsById(1)).thenReturn(true);
            Mockito.when(taxiDriverRepository.findById(1)).thenReturn(Optional.of(getTaxiDriver()));
            Mockito.doNothing().when(taxiDriverRepository).deleteById(1);

            mockMvc.perform(delete("/api/taxi-driver/{id}", 1))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        public void deleteTaxiDriver_NotFound_ReturnsNotFound() throws Exception {
            Mockito.when(taxiDriverRepository.existsById(999)).thenReturn(false);

            mockMvc.perform(delete("/api/taxi-driver/{id}", 999))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    //Util method
    private TaxiDriver getTaxiDriver() {
        return new TaxiDriver(1, "John", "Doe", null, 35,
                5, 30000L, "Toyota Corolla");
    }

    @Nested
    class ListOfTaxiDriverTest {
        private TaxiDriverPaginationFilterRequest request;

        @BeforeEach
        public void setUp() {
            request = getRequest();
            Mockito.when(taxiDriverRepository.findByCompanyAndAge(
                            any(),
                            any(),
                            any(),
                            any(PageRequest.class)))
                    .thenAnswer(invocation -> {
                        Integer minAge = invocation.getArgument(1);
                        Integer maxAge = invocation.getArgument(2);
                        if (minAge == 100 && maxAge == 100) {
                            return new PageImpl<>(Collections.emptyList(),
                                    PageRequest.of(request.getPage() - 1, request.getSize()),
                                    0);
                        }
                        return new PageImpl<>(Collections.singletonList(getTaxiDriver()));
                    });
        }

        @Test
        public void getListOfTaxiDriver_Valid_ReturnsOk() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/taxi-driver/_list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.taxiDrivers").isArray())
                    .andExpect(jsonPath("$.taxiDrivers[0].name").value("John"))
                    .andExpect(jsonPath("$.taxiDrivers[0].age").value(35))
                    .andDo(print());
        }

        @Test
        public void getListOfTaxiDriver_InValid_ReturnsBadRequest() throws Exception {
            request.setPage(100);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/taxi-driver/_list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        public void getListOfTaxiDriver_Valid_ReturnsEmptyList() throws Exception {
            request.setMaxAge(100);
            request.setMinAge(100);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/taxi-driver/_list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPages").value(0))
                    .andExpect(jsonPath("$.taxiDrivers").isArray())
                    .andExpect(jsonPath("$.taxiDrivers").isEmpty())
                    .andDo(print());
        }

        private TaxiDriverPaginationFilterRequest getRequest() {
            TaxiDriverPaginationFilterRequest request = new TaxiDriverPaginationFilterRequest();
            request.setPage(1);
            request.setSize(10);
            request.setCompanyId(1);
            request.setMinAge(25);
            request.setMaxAge(35);
            return request;
        }

    }


    @Nested
    class UploadTaxiDriverTest {
        @MockBean
        private TaxiDriverService taxiDriverService;


        @Test
        public void uploadTaxiDrivers_FileIsEmpty_ReturnsBadRequest() throws Exception {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "file.json", "text/plain", new byte[0]);
            mockMvc.perform(multipart("/api/taxi-driver/upload").file(emptyFile))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("File with drivers is empty")))
                    .andDo(print());
        }

        @Test
        public void uploadTaxiDrivers_InvalidFile_ReturnsBadRequest() throws Exception {
            String jsonString = getJsonString();
            MockMultipartFile file = new MockMultipartFile("file", "test.json",
                    "application/json", jsonString.substring(0, jsonString.length()-2).getBytes());

            mockMvc.perform(multipart("/api/taxi-driver/upload").file(file))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        public void uploadTaxiDrivers_ValidFile_ReturnsSuccess() throws Exception {
            MockMultipartFile file = getMockMultipartFile();
            UploadJsonEntitiesResponse response = new UploadJsonEntitiesResponse(5, 0);
            Mockito.when(taxiDriverService.uploadTaxiDriversFromFile(any(MultipartFile.class)))
                    .thenReturn(response);

            mockMvc.perform(multipart("/api/taxi-driver/upload").file(file))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.successful").value(1))
                    .andExpect(jsonPath("$.failed").value(0))
                    .andDo(print());
        }

        private String getJsonString() {
            return """
                    [{
                    "name":"John",
                    "surname":"Doe",
                    "companyId":1,
                    "age":30,
                    "drivingExperience":5,
                    "salary":30000,
                    "cars":"Toyota"}]
                    """;
        }

        private MockMultipartFile getMockMultipartFile() {
            return new MockMultipartFile("file", "test.json", "application/json", getJsonString().getBytes());
        }

    }


    @Nested
    class DownloadTaxiDriverReportTest {
        @Test
        public void downloadTaxiDriverReport_AnyRequest_ShouldGenerateCsv() throws Exception {
            TaxiDriverFilterRequest filterRequest = new TaxiDriverFilterRequest();
            filterRequest.setMinAge(25);
            filterRequest.setMaxAge(40);


            Mockito.when(taxiDriverRepository.findByCompanyAndAge(any(), any(), any()))
                    .thenReturn(getListWithDrivers());

            String expectedContent = """
                    ID,Name,Surname,CompanyId,Age\r
                    1,John,Doe,,30\r
                    2,Jane,Doe,,35\r
                    3,John,Doe,,40\r
                    """;


            MockHttpServletResponse response = mockMvc.perform(post("/api/taxi-driver/_report")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(filterRequest)))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"drivers.csv\""))
                    .andReturn().getResponse();

            String content = response.getContentAsString();
            assertEquals(expectedContent, content);
        }

        private List<TaxiDriver> getListWithDrivers(){
            TaxiDriver driver1 = getTaxiDriver();
            TaxiDriver driver2 = getTaxiDriver();
            TaxiDriver driver3 = getTaxiDriver();
            driver1.setAge(30);
            driver2.setAge(35);
            driver2.setId(2);
            driver2.setName("Jane");
            driver3.setAge(40);
            driver3.setId(3);
            return List.of(driver1, driver2, driver3);
        }
    }

}