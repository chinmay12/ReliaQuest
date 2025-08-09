package com.reliaquest.api.employee.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.employee.exception.MockServerException;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import com.reliaquest.api.employee.service.EmployeeReaderService;
import com.reliaquest.api.employee.service.external.EmployeeRestClientService;
import com.reliaquest.api.external.model.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmployeeReaderServiceImpl implements EmployeeReaderService {

    private final EmployeeRestClientService employeeRestClientService;

    private final ObjectMapper objectMapper;

    private static final String EMPLOYEE_SERVICE_URI = "/api/v1/employee";

    public EmployeeReaderServiceImpl(EmployeeRestClientService employeeRestClientService, ObjectMapper objectMapper) {
        this.employeeRestClientService = employeeRestClientService;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<EmployeeResponseDto> findAll() {
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(EMPLOYEE_SERVICE_URI, HttpMethod.GET, null, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        List<LinkedHashMap<String, Object>> mockEmployees =
                (List<LinkedHashMap<String, Object>>) response.getBody().data();
        List<EmployeeResponseDto> employeeResponseDtos = mockEmployees.stream()
                .map(e -> objectMapper.convertValue(e, EmployeeResponseDto.class))
                .collect(Collectors.toList());
        return employeeResponseDtos;
    }

    @Override
    public Optional<List<EmployeeResponseDto>> searchByName(String searchString) {
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(EMPLOYEE_SERVICE_URI, HttpMethod.GET, null, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return Optional.empty();
        }
        List<LinkedHashMap<String, Object>> mockEmployees =
                (List<LinkedHashMap<String, Object>>) response.getBody().data();
        List<EmployeeResponseDto> employeeResponseDtos = mockEmployees.stream()
                .map(e -> objectMapper.convertValue(e, EmployeeResponseDto.class))
                .filter(e -> e.getName().contains(searchString))
                .collect(Collectors.toList());
        return employeeResponseDtos.isEmpty() ? Optional.empty() : Optional.of(employeeResponseDtos);
    }

    @Override
    public Optional<EmployeeResponseDto> findById(String id) {
        String finalUri = UriComponentsBuilder.fromUriString("/api/v1/employee/{id}")
                .buildAndExpand(Map.of("id", id))
                .toUriString();
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(finalUri, HttpMethod.GET, null, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            return Optional.empty();
        }
        LinkedHashMap<String, Object> employee =
                (LinkedHashMap<String, Object>) response.getBody().data();
        EmployeeResponseDto employeeResponseDto = objectMapper.convertValue(employee, EmployeeResponseDto.class);
        return employeeResponseDto != null ? Optional.of(employeeResponseDto) : Optional.empty();
    }
}
