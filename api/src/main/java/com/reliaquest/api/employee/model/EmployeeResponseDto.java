package com.reliaquest.api.employee.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;
import lombok.*;

// EmployeeResponseDto.java

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(EmployeeResponseDto.PrefixNamingStrategy.class)
public class EmployeeResponseDto {
    private UUID id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;

    static class PrefixNamingStrategy extends PropertyNamingStrategies.NamingBase {

        @Override
        public String translate(String propertyName) {
            if ("id".equals(propertyName)) {
                return propertyName;
            }
            return "employee_" + propertyName;
        }
    }
}
