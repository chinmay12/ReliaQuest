package com.reliaquest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.annotation.Annotation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ApiApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void allCoreBeansShouldBeInitialized() {
        verifyBeansForAnnotation(Service.class);
    }

    @Test
    void swaggerUiLoads() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    /**
     * Verify OpenAPI docs endpoint is accessible.
     */
    @Test
    void openApiDocsAvailable() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    private void verifyBeansForAnnotation(Class<?> annotationClass) {
        String[] beanNames =
                applicationContext.getBeanNamesForAnnotation((Class<? extends Annotation>) annotationClass);

        assertThat(beanNames)
                .as("No beans found with annotation %s", annotationClass.getSimpleName())
                .isNotEmpty();

        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            assertThat(bean)
                    .as("Bean '%s' with annotation %s should not be null", beanName, annotationClass.getSimpleName())
                    .isNotNull();
        }
    }
}
