package com.reliaquest.api.employee.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeRequestDto {
    @NotBlank(message = "Name must not be blank")
    String name;

    @NotNull(message = "Salary is required") @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    BigDecimal salary;

    @NotNull(message = "Age is required") @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 75, message = "Age must be at most 75")
    Integer age;

    @NotBlank(message = "Title must not be blank")
    String title;
}
