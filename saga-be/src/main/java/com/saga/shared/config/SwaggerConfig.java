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
                                                                                                .description("Vui lòng nhập **JWT Token** (được trả về từ API Login) vào đây để test các API yêu cầu xác thực.")))
                                // Ép thứ tự hiển thị của các Tag trên Swagger UI
                                .tags(List.of(
                                                new Tag().name("1. Auth APIs")
                                                                .description("1. Đăng nhập, Đăng xuất, Refresh Token"),
                                                new Tag().name("2. Identity Mapping APIs")
                                                                .description("2. Liên kết tài khoản Jira/GitHub"),
                                                new Tag().name("4. Admin - Academic APIs")
                                                                .description("Endpoints for Admin to manage Semesters and Courses"),
                                                new Tag().name("5. Lecturer - Academic & Course APIs")
                                                                .description("4. Giảng viên quản lý nhóm sinh viên"),
                                                new Tag().name("8. Team Leader - Integrations")
                                                                .description("5. Team Leader liên kết dự án"),
                                                new Tag().name("6. Lecturer - Project Progress APIs")
                                                                .description("Endpoints for Lecturers to view team project progress"),
                                                new Tag().name("7. Student APIs")
                                                                .description("Endpoints for Students to view the Jira/GitHub progress of their teams"),
                                                new Tag().name("9. Webhooks")
                                                                .description("8. Hệ thống nhận webhook (Internal)")));
        }
}
