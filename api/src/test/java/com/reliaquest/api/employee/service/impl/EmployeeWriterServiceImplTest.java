package com.reliaquest.api.employee.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.employee.exception.MockServerException;
import com.reliaquest.api.employee.model.EmployeeRequestDto;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import com.reliaquest.api.employee.service.external.EmployeeRestClientService;
import com.reliaquest.api.external.model.DeleteMockEmployeeInput;
import com.reliaquest.api.external.model.Response;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EmployeeWriterServiceImplTest {

    @Mock
    private EmployeeRestClientService employeeRestClientService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EmployeeWriterServiceImpl employeeWriterService;

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
    void create_success() {
        EmployeeRequestDto request = new EmployeeRequestDto("John", new BigDecimal(45), 45, "Head");
        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("name", "John");

        EmployeeResponseDto dto = new EmployeeResponseDto();
        dto.setName("John");

        when(employeeRestClientService.callApi(
                        eq("/api/v1/employee"), eq(HttpMethod.POST), eq(request), isNull(), eq(Response.class)))
                .thenReturn(buildResponse(empMap, Response.Status.HANDLED, HttpStatus.OK, null));

        when(objectMapper.convertValue(empMap, EmployeeResponseDto.class)).thenReturn(dto);

        EmployeeResponseDto result = employeeWriterService.create(request);

        assertEquals("John", result.getName());
    }

    @Test
    void create_errorStatus_throwsException() {
        EmployeeRequestDto request = new EmployeeRequestDto("John", new BigDecimal(45), 45, "Head");

        when(employeeRestClientService.callApi(anyString(), eq(HttpMethod.POST), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(
                        null, Response.Status.ERROR, HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request"));

        assertThrows(MockServerException.class, () -> employeeWriterService.create(request));
    }

    @Test
    void deleteById_success_employeeDeletedTrue() {
        String empId = "123";

        // First GET call to fetch employee
        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("employee_name", "John");

        when(employeeRestClientService.callApi(
                        contains("/api/v1/employee/123"), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(empMap, Response.Status.HANDLED, HttpStatus.OK, null));

        // Second DELETE call to delete employee
        when(employeeRestClientService.callApi(
                        eq("/api/v1/employee"),
                        eq(HttpMethod.DELETE),
                        any(DeleteMockEmployeeInput.class),
                        any(),
                        eq(Response.class)))
                .thenReturn(buildResponse(true, Response.Status.HANDLED, HttpStatus.OK, null));

        Optional<String> result = employeeWriterService.deleteById(empId);

        assertTrue(result.isPresent());
        assertEquals("John", result.get());
    }

    @Test
    void deleteById_success_employeeDeletedFalse() {
        String empId = "123";

        LinkedHashMap<String, Object> empMap = new LinkedHashMap<>();
        empMap.put("employee_name", "John");

        when(employeeRestClientService.callApi(
                        contains("/api/v1/employee/123"), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(empMap, Response.Status.HANDLED, HttpStatus.OK, null));

        when(employeeRestClientService.callApi(
                        eq("/api/v1/employee"),
                        eq(HttpMethod.DELETE),
                        any(DeleteMockEmployeeInput.class),
                        any(),
                        eq(Response.class)))
                .thenReturn(buildResponse(false, Response.Status.HANDLED, HttpStatus.OK, null));

        Optional<String> result = employeeWriterService.deleteById(empId);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_employeeNotFound_returnsEmpty() {
        String empId = "123";

        when(employeeRestClientService.callApi(
                        contains("/api/v1/employee/123"), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(null, Response.Status.HANDLED, HttpStatus.NOT_FOUND, null));

        Optional<String> result = employeeWriterService.deleteById(empId);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_errorStatusOnFetch_throwsException() {
        when(employeeRestClientService.callApi(
                        contains("/api/v1/employee/123"), eq(HttpMethod.GET), any(), any(), eq(Response.class)))
                .thenReturn(buildResponse(null, Response.Status.ERROR, HttpStatus.OK, "Error processing request"));

        assertThrows(MockServerException.class, () -> employeeWriterService.deleteById("123"));
    }
}
