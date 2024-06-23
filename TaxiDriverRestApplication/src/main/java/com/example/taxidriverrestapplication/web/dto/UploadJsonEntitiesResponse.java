package com.example.taxidriverrestapplication.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class UploadJsonEntitiesResponse {
    private int successful;
    private int failed;
}
