package com.ticketing.orderservice.util;

import com.ticketing.orderservice.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateTokenAndGetClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired JWT token");
        }
    }

    public UUID getBuyerIdFromToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        String buyerIdString = claims.get("buyer_id", String.class);
        if (buyerIdString == null) {
            throw new UnauthorizedException("Token does not contain buyer_id");
        }
        return UUID.fromString(buyerIdString);
    }

    public String getRoleFromToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        String role = claims.get("role", String.class);
        if (role == null) {
            throw new UnauthorizedException("Token does not contain role");
        }
        return role;
    }

    public void validateBuyerRole(String token) {
        String role = getRoleFromToken(token);
        if (!"BUYER".equals(role)) {
            throw new UnauthorizedException("BUYER role required");
        }
    }
}
