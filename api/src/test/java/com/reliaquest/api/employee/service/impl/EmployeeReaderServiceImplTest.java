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

class EmployeeReaderServiceImplTest {

    @Mock
    private EmployeeRestClientService employeeRestClientService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeReaderServiceImpl employeeReaderService;

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
    void findAll_success() {
        // Arrange
        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("name", "John");

        List<LinkedHashMap<String, Object>> employees = List.of(empMap);

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setName("John");

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(employees, Response.Status.HANDLED, HttpStatus.OK, null));

        when(objectMapper.convertValue(empMap, EmployeeResponseDto.class)).thenReturn(dto);

        // Act
        List<EmployeeResponseDto> result = employeeReaderService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
    }

    @Test
    void findAll_errorStatus_throwsException() {
        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(
                        null, Response.Status.ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "Exception while processing"));

        assertThrows(MockServerException.class, () -> employeeReaderService.findAll());
    }

    @Test
    void searchByName_matchFound() {
        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("name", "Alice");

        List<LinkedHashMap<String, Object>> employees = List.of(empMap);

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setName("Alice");

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(employees, Response.Status.HANDLED, HttpStatus.OK, null));

        when(objectMapper.convertValue(empMap, EmployeeResponseDto.class)).thenReturn(dto);

        Optional<List<EmployeeResponseDto>> result = employeeReaderService.searchByName("Alice");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void searchByName_notFound_returnsEmpty() {
        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("name", "Bob");

        List<LinkedHashMap<String, Object>> employees = List.of(empMap);

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setName("Bob");

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(employees, Response.Status.HANDLED, HttpStatus.OK, null));

        when(objectMapper.convertValue(empMap, EmployeeResponseDto.class)).thenReturn(dto);

        Optional<List<EmployeeResponseDto>> result = employeeReaderService.searchByName("Alice");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchByName_httpNotFound_returnsEmptyOptional() {
        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(null, Response.Status.HANDLED, HttpStatus.NOT_FOUND, null));

        Optional<List<EmployeeResponseDto>> result = employeeReaderService.searchByName("Test");

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_success() {
        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("name", "Charlie");

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setName("Charlie");

        when(employeeRestClientService.callApi(
                        contains("/api/v1/employee/123"), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(empMap, Response.Status.HANDLED, HttpStatus.OK, null));

        when(objectMapper.convertValue(empMap, EmployeeResponseDto.class)).thenReturn(dto);

        Optional<EmployeeResponseDto> result = employeeReaderService.findById("123");

        assertTrue(result.isPresent());
        assertEquals("Charlie", result.get().getName());
    }

    @Test
    void findById_errorStatus_throwsException() {
        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(null, Response.Status.ERROR, HttpStatus.OK, "Error processing request"));

        assertThrows(MockServerException.class, () -> employeeReaderService.findById("123"));
    }
}
