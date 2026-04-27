package com.slingshot.carshowroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response body for successful login")
public record LoginResponse(
        @Schema(example = "john.doe") 
        String userId,
        
        @Schema(example = "John Doe") 
        String name,
        
        @Schema(description = "CUSTOMER | STAFF | MANAGER") 
        String role
) {}
