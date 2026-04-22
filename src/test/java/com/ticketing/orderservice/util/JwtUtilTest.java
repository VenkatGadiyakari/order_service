package com.ticketing.orderservice.util;

import com.ticketing.orderservice.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        testSecret = "test-secret-key-minimum-32-characters-long-for-hmac-sha256";
        jwtUtil = new JwtUtil(testSecret);
        secretKey = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void testValidateTokenAndGetClaims() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .claim("role", "BUYER")
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        assertNotNull(claims);
        assertEquals(buyerId.toString(), claims.get("buyer_id", String.class));
        assertEquals("BUYER", claims.get("role", String.class));
    }

    @Test
    void testGetBuyerIdFromToken() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .claim("role", "BUYER")
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        UUID extractedBuyerId = jwtUtil.getBuyerIdFromToken(token);

        assertEquals(buyerId, extractedBuyerId);
    }

    @Test
    void testGetRoleFromToken() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .claim("role", "BUYER")
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        String role = jwtUtil.getRoleFromToken(token);

        assertEquals("BUYER", role);
    }

    @Test
    void testValidateBuyerRole() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .claim("role", "BUYER")
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        assertDoesNotThrow(() -> jwtUtil.validateBuyerRole(token));
    }

    @Test
    void testValidateBuyerRoleWithInvalidRole() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .claim("role", "ORGANISER")
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateBuyerRole(token));
    }

    @Test
    void testValidateTokenWithExpiredToken() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .claim("role", "BUYER")
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(secretKey)
                .compact();

        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateTokenAndGetClaims(token));
    }

    @Test
    void testGetBuyerIdFromTokenWithoutBuyerId() {
        String token = Jwts.builder()
                .claim("role", "BUYER")
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        assertThrows(UnauthorizedException.class, () -> jwtUtil.getBuyerIdFromToken(token));
    }

    @Test
    void testGetRoleFromTokenWithoutRole() {
        UUID buyerId = UUID.randomUUID();
        String token = Jwts.builder()
                .claim("buyer_id", buyerId.toString())
                .setExpiration(new Date(System.currentTimeMillis() + 900000))
                .signWith(secretKey)
                .compact();

        assertThrows(UnauthorizedException.class, () -> jwtUtil.getRoleFromToken(token));
    }

    @Test
    void testValidateTokenWithInvalidSignature() {
        String invalidToken = "invalid.token.signature";

        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateTokenAndGetClaims(invalidToken));
    }

    @Test
    void testValidateTokenWithMalformedToken() {
        String malformedToken = "not-a-valid-jwt-token";

        assertThrows(UnauthorizedException.class, () -> jwtUtil.validateTokenAndGetClaims(malformedToken));
    }
}
