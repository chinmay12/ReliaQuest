package com.reliaquest.api.employee.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.employee.exception.MockServerException;
import com.reliaquest.api.employee.model.EmployeeRequestDto;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import com.reliaquest.api.employee.service.EmployeeWriterService;
import com.reliaquest.api.employee.service.external.EmployeeRestClientService;
import com.reliaquest.api.external.model.DeleteMockEmployeeInput;
import com.reliaquest.api.external.model.Response;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmployeeWriterServiceImpl implements EmployeeWriterService {

    private final EmployeeRestClientService employeeRestClientService;

    private final ObjectMapper objectMapper;

    private static final String EMPLOYEE_SERVICE_URI = "/api/v1/employee";

    public EmployeeWriterServiceImpl(EmployeeRestClientService employeeRestClientService, ObjectMapper objectMapper) {
        this.employeeRestClientService = employeeRestClientService;
        this.objectMapper = objectMapper;
    }

    @Override
    public EmployeeResponseDto create(EmployeeRequestDto input) {
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(EMPLOYEE_SERVICE_URI, HttpMethod.POST, input, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        LinkedHashMap<String, Object> mockEmployeeCreated =
                (LinkedHashMap<String, Object>) response.getBody().data();
        EmployeeResponseDto employeeResponseDto =
                objectMapper.convertValue(mockEmployeeCreated, EmployeeResponseDto.class);
        return employeeResponseDto;
    }

    @Override
    public Optional<String> deleteById(String id) {
        String finalUriFetch = UriComponentsBuilder.fromUriString("/api/v1/employee/{id}")
                .buildAndExpand(Map.of("id", id))
                .toUriString();
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(finalUriFetch, HttpMethod.GET, null, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        LinkedHashMap<String, Object> employee =
                (LinkedHashMap<String, Object>) response.getBody().data();
        if (employee == null || response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return Optional.empty();
        }

        DeleteMockEmployeeInput deleteEmployeeDetails = new DeleteMockEmployeeInput();
        deleteEmployeeDetails.setName((String) employee.get("employee_name"));
        response = employeeRestClientService.callApi(
                EMPLOYEE_SERVICE_URI, HttpMethod.DELETE, deleteEmployeeDetails, null, Response.class);
        Boolean employeeDeleted = (Boolean) response.getBody().data();

        return employeeDeleted.equals(Boolean.TRUE)
                ? Optional.of((String) employee.get("employee_name"))
                : Optional.empty();
    }
}
