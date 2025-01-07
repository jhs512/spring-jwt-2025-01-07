package com.ll.spring_jwt_2025_01_07.standard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;

import java.security.Key;
import java.util.Date;
import java.util.Map;

public class Ut {
    public static class str {
        public static boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }
    }

    public static class json {
        private static final ObjectMapper om = new ObjectMapper();

        @SneakyThrows
        public static String toString(Object obj) {
            return om.writeValueAsString(obj);
        }
    }

    public static class jwt {
        public static String toString(String secret, int expireSeconds, Map<String, Object> body) {
            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());

            String jwt = Jwts.builder()
                    .claims(body)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();

            return jwt;
        }
    }
}
