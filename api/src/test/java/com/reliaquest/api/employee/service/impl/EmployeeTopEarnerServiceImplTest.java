package com.reliaquest.api.employee.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.employee.exception.MockServerException;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import com.reliaquest.api.employee.service.external.EmployeeRestClientService;
import com.reliaquest.api.external.model.Response;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EmployeeTopEarnerServiceImplTest {

    @Mock
    private EmployeeRestClientService employeeRestClientService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeTopEarnerServiceImpl employeeTopEarnerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ResponseEntity<Response> buildResponse(
            Object data, Response.Status status, HttpStatus httpStatus, String error) {
        Response mockResponse = new Response(data, status, error);
        return new ResponseEntity<>(mockResponse, httpStatus);
    }

    @Test
    void highestSalary_success() {
        LinkedHashMap<String, Object> empMap1 = new LinkedHashMap<>();
        empMap1.put("name", "John");
        empMap1.put("salary", 1000);

        LinkedHashMap<String, Object> empMap2 = new LinkedHashMap<>();
        empMap2.put("name", "Alice");
        empMap2.put("salary", 2000);

        List<LinkedHashMap<String, Object>> employees = List.of(empMap1, empMap2);

        EmployeeResponseDto dto1 = new EmployeeResponseDto();
        dto1.setName("John");
        dto1.setSalary(1000);

        EmployeeResponseDto dto2 = new EmployeeResponseDto();
        dto2.setName("Alice");
        dto2.setSalary(2000);

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(employees, Response.Status.HANDLED, HttpStatus.OK, null));

        when(objectMapper.convertValue(empMap1, EmployeeResponseDto.class)).thenReturn(dto1);
        when(objectMapper.convertValue(empMap2, EmployeeResponseDto.class)).thenReturn(dto2);

        Optional<Integer> result = employeeTopEarnerService.highestSalary();

        assertTrue(result.isPresent());
        assertEquals(2000, result.get());
    }

    @Test
    void highestSalary_emptyList_returnsEmptyOptional() {
        List<LinkedHashMap<String, Object>> employees = Collections.emptyList();

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(employees, Response.Status.HANDLED, HttpStatus.OK, null));

        Optional<Integer> result = employeeTopEarnerService.highestSalary();

        assertTrue(result.isEmpty());
    }

    @Test
    void highestSalary_errorStatus_throwsException() {
        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(null, Response.Status.ERROR, HttpStatus.OK, "Error processing request"));

        assertThrows(MockServerException.class, () -> employeeTopEarnerService.highestSalary());
    }

    @Test
    void topTenHighestEarningNames_success() {
        List<LinkedHashMap<String, Object>> employees = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
            empMap.put("name", "Emp" + i);
            empMap.put("salary", i * 100);
            employees.add(empMap);

            EmployeeResponseDto dto = new EmployeeResponseDto();
            dto.setName("Emp" + i);
            dto.setSalary(i * 100);

            when(objectMapper.convertValue(empMap, EmployeeResponseDto.class)).thenReturn(dto);
        }

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(employees, Response.Status.HANDLED, HttpStatus.OK, null));

        List<String> result = employeeTopEarnerService.topTenHighestEarningNames();

        assertEquals(10, result.size());
        assertEquals("Emp12", result.get(0)); // highest salary
    }

    @Test
    void topTenHighestEarningNames_errorStatus_throwsException() {
        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(null, Response.Status.ERROR, HttpStatus.OK, null));

        assertThrows(MockServerException.class, () -> employeeTopEarnerService.topTenHighestEarningNames());
    }
}
