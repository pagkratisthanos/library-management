package com.library.management.core;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library Management API")
                        .version("1.0.0")
                        .description("""
                                REST API for managing a Library Management System.
                                Provides endpoints for managing authors, books, members, copies and rentals.
                                
                                Authentication is done via JWT Bearer tokens.
                                Obtain a token from /api/v1/auth/authenticate before using secured endpoints.
                                
                                Errors answer with {"code": "...", "description": "..."}.
                                Validation failures add an "errors" map of field to message.
                        """)
                        .contact(new Contact()
                                .name("Thanos Pagkratis")
                                .email("thanos@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }

    /**
     * Adds the error responses every secured endpoint can return.
     * This composes with the responses springdoc derives from the return type,
     * whereas a declared @ApiResponses would replace them.
     */
    @Bean
    public OperationCustomizer globalSecurityResponses() {
        return (operation, handlerMethod) -> {
            boolean isSecured = handlerMethod.hasMethodAnnotation(SecurityRequirement.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(SecurityRequirement.class);

            if (isSecured) {
                operation.getResponses()
                        .addApiResponse("400", new ApiResponse()
                                .description("Validation error or a broken business rule"))
                        .addApiResponse("401", new ApiResponse()
                                .description("Unauthorized - JWT token is missing or invalid"))
                        .addApiResponse("403", new ApiResponse()
                                .description("Forbidden - You don't have permission to access this resource"))
                        .addApiResponse("404", new ApiResponse()
                                .description("Entity not found"))
                        .addApiResponse("409", new ApiResponse()
                                .description("Entity already exists"))
                        .addApiResponse("500", new ApiResponse()
                                .description("Unexpected error"));
            }
            return operation;
        };
    }
}