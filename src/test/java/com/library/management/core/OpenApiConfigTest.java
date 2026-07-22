package com.library.management.core;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void customOpenAPI_shouldReturnOpenAPIWithCorrectTitle() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Library Management API");
    }

    @Test
    void customOpenAPI_shouldReturnOpenAPIWithCorrectVersion() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void customOpenAPI_shouldReturnOpenAPIWithContact() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Thanos Pagkratis");
    }

    @Test
    void globalSecurityResponses_whenSecuredMethod_shouldAdd401And403() throws NoSuchMethodException {
        OperationCustomizer customizer = openApiConfig.globalSecurityResponses();

        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.hasMethodAnnotation(SecurityRequirement.class)).thenReturn(true);

        customizer.customize(operation, handlerMethod);

        assertThat(operation.getResponses()).containsKey("401");
        assertThat(operation.getResponses()).containsKey("403");
    }

    @Test
    void globalSecurityResponses_whenNotSecuredMethod_shouldNotAdd401And403() {
        OperationCustomizer customizer = openApiConfig.globalSecurityResponses();

        Operation operation = new Operation();
        operation.setResponses(new ApiResponses());

        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.hasMethodAnnotation(SecurityRequirement.class)).thenReturn(false);
        when(handlerMethod.getBeanType()).thenReturn((Class) Object.class);

        customizer.customize(operation, handlerMethod);

        assertThat(operation.getResponses()).doesNotContainKey("401");
        assertThat(operation.getResponses()).doesNotContainKey("403");
    }
}