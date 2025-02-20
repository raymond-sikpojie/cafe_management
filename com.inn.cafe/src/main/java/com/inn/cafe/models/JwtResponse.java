package com.inn.cafe.models;

import lombok.Data;

@Data
public class JwtResponse {
    private String message;
    private String token;
}
