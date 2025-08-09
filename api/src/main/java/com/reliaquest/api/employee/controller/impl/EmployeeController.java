package com.reliaquest.api.employee.controller.impl;

import com.reliaquest.api.employee.controller.IEmployeeController;
import com.reliaquest.api.employee.model.EmployeeRequestDto;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import com.reliaquest.api.employee.service.EmployeeReaderService;
import com.reliaquest.api.employee.service.EmployeeTopEarnerService;
import com.reliaquest.api.employee.service.EmployeeWriterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController implements IEmployeeController<EmployeeResponseDto, EmployeeRequestDto> {

    private final EmployeeReaderService reader;
    private final EmployeeWriterService writer;
    private final EmployeeTopEarnerService topEarner;

    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    public EmployeeController(
            EmployeeReaderService reader, EmployeeWriterService writer, EmployeeTopEarnerService topEarner) {
        this.reader = reader;
        this.writer = writer;
        this.topEarner = topEarner;
    }

    @Override
    @Operation(summary = "Get all employees", description = "Fetches a list of all employees")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of employees retrieved successfully",
                        content =
                                @Content(
                                        array =
                                                @ArraySchema(
                                                        schema = @Schema(implementation = EmployeeResponseDto.class)))),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    public ResponseEntity<List<EmployeeResponseDto>> getAllEmployees() {
        List<EmployeeResponseDto> employeeResponseDtos = reader.findAll();
        return ResponseEntity.ok(employeeResponseDtos);
    }

    @Override
    @Operation(
            summary = "Search employees by name",
            description = "Returns employees whose names contain the search string")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Employees found",
                        content =
                                @Content(
                                        array =
                                                @ArraySchema(
                                                        schema = @Schema(implementation = EmployeeResponseDto.class)))),
                @ApiResponse(responseCode = "404", description = "No employees found")
            })
    public ResponseEntity<List<EmployeeResponseDto>> getEmployeesByNameSearch(@PathVariable String searchString) {
        Optional<List<EmployeeResponseDto>> employeeResponseDtos = reader.searchByName(searchString);
        return employeeResponseDtos.isPresent()
                ? ResponseEntity.ok(employeeResponseDtos.get())
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    @Operation(summary = "Get employee by ID", description = "Fetches details of a specific employee")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Employee found",
                        content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                @ApiResponse(responseCode = "404", description = "Employee not found")
            })
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable String id) {
        Optional<EmployeeResponseDto> employeeResponseDto = reader.findById(id);
        return employeeResponseDto.isPresent()
                ? ResponseEntity.ok(employeeResponseDto.get())
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    @Operation(summary = "Get highest salary", description = "Fetches the highest salary among all employees")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Optional<Integer> highestSalary = topEarner.highestSalary();
        return highestSalary.isPresent()
                ? ResponseEntity.ok(highestSalary.get())
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Override
    @Operation(
            summary = "Get top 10 highest earning employees",
            description = "Fetches names of the top 10 highest earning employees")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(topEarner.topTenHighestEarningNames());
    }

    @Override
    @Operation(summary = "Create a new employee", description = "Adds a new employee to the system")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Employee created successfully",
                        content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input provided")
            })
    public ResponseEntity<EmployeeResponseDto> createEmployee(@Valid @RequestBody EmployeeRequestDto employeeInput) {
        EmployeeResponseDto created = writer.create(employeeInput);
        return ResponseEntity.created(URI.create("/employees/" + created.getId()))
                .body(created);
    }

    @Override
    @Operation(summary = "Delete employee by ID", description = "Deletes an employee from the system")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Employee deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Employee not found")
            })
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        Optional<String> deleted = writer.deleteById(id);
        return deleted.isPresent()
                ? ResponseEntity.ok(deleted.get())
                : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
