package com.saga.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                String securitySchemeName = "Bearer Authentication";

                return new OpenAPI()
                                .info(new Info()
                                                .title("SAGA API Documentation")
                                                .version("1.0")
                                                .description("Tài liệu API chi tiết cho dự án SAGA Backend.\n\n" +
                                                                "Dự án áp dụng mô hình **Clean Architecture**. " +
                                                                "API được thiết kế chuẩn RESTful, hỗ trợ phân quyền và xác thực bằng **JWT**.")
                                                .contact(new Contact().name("Backend Team")
                                                                .email("saga-team@example.com")))
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(
                                                new Components()
                                                                .addSecuritySchemes(securitySchemeName,
                                                                                new SecurityScheme()
                                                                                                .name(securitySchemeName)
                                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                                .scheme("bearer")
                                                                                                .bearerFormat("JWT")
                                                                                                .description("Vui lòng nhập **JWT Token** (được trả về từ API Login) vào đây để test các API yêu cầu xác thực.")));
        }
}
