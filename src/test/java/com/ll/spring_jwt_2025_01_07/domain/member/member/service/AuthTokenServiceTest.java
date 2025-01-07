package com.ll.spring_jwt_2025_01_07.domain.member.member.service;

import com.ll.spring_jwt_2025_01_07.domain.member.member.entity.Member;
import com.ll.spring_jwt_2025_01_07.standard.util.Ut;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthTokenServiceTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthTokenService authTokenService;

    // 테스트용 토큰 만료기간 : 1년
    private int expireSeconds = 60 * 60 * 24 * 365;
    // 테스트용 토큰 시크릿 키
    private String secret = "abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890";

    @Test
    @DisplayName("authTokenService 서비스가 존재한다.")
    void t1() {
        assertThat(authTokenService).isNotNull();
    }

    @Test
    @DisplayName("jjwt 로 JWT 생성, {name=\"Paul\", age=23}")
    void t2() {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

        Map<String, Object> payload = Map.of(
                "name", "Paul",
                "age", 23
        );

        String jwtStr = Jwts.builder()
                .claims(payload)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        assertThat(jwtStr).isNotBlank();

        // 키가 유효한지 테스트
        Map<String, Object> parsedPayload = (Map<String, Object>) Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parse(jwtStr)
                .getPayload();

        // 키로 부터 payload 를 파싱한 결과가 원래 payload 와 같은지 테스트
        assertThat(parsedPayload)
                .containsAllEntriesOf(payload);
    }

    @Test
    @DisplayName("Ut.jwt.toString 를 통해서 JWT 생성, {name=\"Paul\", age=23}")
    void t3() {
        Map<String, Object> payload = Map.of("name", "Paul", "age", 23);

        String jwtStr = Ut.jwt.toString(secret, expireSeconds, payload);

        assertThat(jwtStr).isNotBlank();
        assertThat(Ut.jwt.isValid(secret, jwtStr)).isTrue();

        Map<String, Object> parsedPayload = Ut.jwt.payload(secret, jwtStr);

        assertThat(parsedPayload).containsAllEntriesOf(payload);
    }

    @Test
    @DisplayName("authTokenService.genAccessToken(member);")
    void t4() {
        Member memberUser1 = memberService.findByUsername("user1").get();

        String accessToken = authTokenService.genAccessToken(memberUser1);

        assertThat(accessToken).isNotBlank();

        assertThat(Ut.jwt.isValid(secret, accessToken)).isTrue();

        Map<String, Object> parsedPayload = authTokenService.payload(secret, accessToken);

        assertThat(parsedPayload)
                .containsAllEntriesOf(
                        Map.of(
                                "id", memberUser1.getId(),
                                "username", memberUser1.getUsername()
                        )
                );
    }
}