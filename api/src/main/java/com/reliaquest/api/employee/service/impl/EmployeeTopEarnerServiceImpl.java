package com.reliaquest.api.employee.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.employee.exception.MockServerException;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import com.reliaquest.api.employee.service.EmployeeTopEarnerService;
import com.reliaquest.api.employee.service.external.EmployeeRestClientService;
import com.reliaquest.api.external.model.Response;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class EmployeeTopEarnerServiceImpl implements EmployeeTopEarnerService {

    private final EmployeeRestClientService employeeRestClientService;

    private final ObjectMapper objectMapper;

    private static final String EMPLOYEE_SERVICE_URI = "/api/v1/employee";

    public EmployeeTopEarnerServiceImpl(
            EmployeeRestClientService employeeRestClientService, ObjectMapper objectMapper) {
        this.employeeRestClientService = employeeRestClientService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Integer> highestSalary() {
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(EMPLOYEE_SERVICE_URI, HttpMethod.GET, null, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        List<LinkedHashMap<String, Object>> mockEmployees =
                (List<LinkedHashMap<String, Object>>) response.getBody().data();
        Optional<Integer> employeeResponseDtos = mockEmployees.stream()
                .map(e -> objectMapper.convertValue(e, EmployeeResponseDto.class))
                .map(e -> e.getSalary())
                .max(Comparator.naturalOrder());
        return employeeResponseDtos;
    }

    @Override
    public List<String> topTenHighestEarningNames() {
        ResponseEntity<Response> response =
                employeeRestClientService.callApi(EMPLOYEE_SERVICE_URI, HttpMethod.GET, null, null, Response.class);
        if (response.getBody().status().equals(Response.Status.ERROR)) {
            throw new MockServerException("Mock server returned error while processing request");
        }
        List<LinkedHashMap<String, Object>> mockEmployees =
                (List<LinkedHashMap<String, Object>>) response.getBody().data();
        List<String> employeeResponseDtos = mockEmployees.stream()
                .map(e -> objectMapper.convertValue(e, EmployeeResponseDto.class))
                .sorted(Comparator.comparingInt(EmployeeResponseDto::getSalary).reversed())
                .limit(10)
                .map(e -> e.getName())
                .collect(Collectors.toList());
        return employeeResponseDtos;
    }
}
