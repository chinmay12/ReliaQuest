package com.reliaquest.api.employee.service;

import com.reliaquest.api.employee.model.EmployeeRequestDto;
import com.reliaquest.api.employee.model.EmployeeResponseDto;
import java.util.Optional;

public interface EmployeeWriterService {
    EmployeeResponseDto create(EmployeeRequestDto input);

    Optional<String> deleteById(String id);
}
