// EmployeeReaderService.java
package com.reliaquest.api.employee.service;

import com.reliaquest.api.employee.model.EmployeeResponseDto;
import java.util.List;
import java.util.Optional;

public interface EmployeeReaderService {
    List<EmployeeResponseDto> findAll();

    Optional<List<EmployeeResponseDto>> searchByName(String searchString);

    Optional<EmployeeResponseDto> findById(String id);
}
