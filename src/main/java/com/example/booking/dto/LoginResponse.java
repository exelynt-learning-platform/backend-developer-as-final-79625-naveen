package com.example.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor public class LoginResponse {
    private String token;
    private String tokenType;
    private long expiresInSeconds;
}
