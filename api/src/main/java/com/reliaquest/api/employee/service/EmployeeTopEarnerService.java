package com.reliaquest.api.employee.service;

import java.util.List;
import java.util.Optional;

public interface EmployeeTopEarnerService {
    Optional<Integer> highestSalary();

    List<String> topTenHighestEarningNames();
}
