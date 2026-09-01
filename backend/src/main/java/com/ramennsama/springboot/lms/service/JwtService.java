package com.ramennsama.springboot.lms.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    String generateToken(UserDetails userDetails);
    boolean isTokenValid(String token);
    io.jsonwebtoken.Claims extractAllClaims(String token);
}
