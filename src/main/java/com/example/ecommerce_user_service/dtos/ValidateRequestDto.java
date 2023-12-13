package com.example.ecommerce_user_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateRequestDto {
    private Long UserId;
    private String token;
}
