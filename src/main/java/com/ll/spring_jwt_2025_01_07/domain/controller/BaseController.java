package com.ll.spring_jwt_2025_01_07.domain.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

public class BaseController {
    protected void setCookie(HttpServletResponse resp, String name, String value) {
        resp.addHeader(
                "Set-Cookie",
                ResponseCookie.from(name, value)
                        .path("/")
                        .domain("localhost")
                        .sameSite("Strict")
                        .secure(true)
                        .httpOnly(true)
                        .build()
                        .toString()
        );
    }
}
