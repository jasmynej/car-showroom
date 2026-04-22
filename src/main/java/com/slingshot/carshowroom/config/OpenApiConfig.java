package com.slingshot.carshowroom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI carShowroomOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Car Showroom Management API")
                        .description("""
                                REST API for managing a car showroom.

                                **Typical purchase workflow:**
                                1. `POST /api/orders` — customer submits a purchase order for an available car
                                2. `PATCH /api/orders/{id}/status` — manager approves (car moves to RESERVED)
                                3. `POST /api/invoices` — staff generates an invoice for the approved order
                                4. `POST /api/payments` — customer pays; car moves to SOLD, order is COMPLETED, sale is recorded

                                **Demo seed data is loaded on startup.** The H2 console is available at `/h2-console`.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("Slingshot").email("admin@showroom.com")))
                .tags(List.of(
                        new Tag().name("Cars").description("Inventory management"),
                        new Tag().name("Users").description("Customers, staff, and managers"),
                        new Tag().name("Purchase Orders").description("Car purchase requests and approvals"),
                        new Tag().name("Invoices").description("Invoice generation for approved orders"),
                        new Tag().name("Payments").description("Payment processing — triggers sale completion"),
                        new Tag().name("Test Drives").description("Test drive scheduling"),
                        new Tag().name("Service Schedules").description("Vehicle service and maintenance"),
                        new Tag().name("Sales").description("Completed sale records (read-only)")
                ));
    }
}
