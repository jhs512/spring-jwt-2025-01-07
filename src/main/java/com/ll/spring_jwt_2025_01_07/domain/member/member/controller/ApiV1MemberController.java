package com.ll.spring_jwt_2025_01_07.domain.member.member.controller;

import com.ll.spring_jwt_2025_01_07.domain.member.member.dto.MemberDto;
import com.ll.spring_jwt_2025_01_07.domain.member.member.entity.Member;
import com.ll.spring_jwt_2025_01_07.domain.member.member.service.AuthTokenService;
import com.ll.spring_jwt_2025_01_07.domain.member.member.service.MemberService;
import com.ll.spring_jwt_2025_01_07.global.exceptions.ServiceException;
import com.ll.spring_jwt_2025_01_07.global.rq.Rq;
import com.ll.spring_jwt_2025_01_07.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final AuthTokenService authTokenService;
    private final Rq rq;

    record MemberJoinReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password,
            @NotBlank
            String nickname
    ) {
    }

    @PostMapping("/join")
    @Transactional
    public RsData<MemberDto> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ) {
        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getName()),
                new MemberDto(member)
        );
    }


    record MemberLoginReqBody(
            @NotBlank
            String username,
            @NotBlank
            String password
    ) {
    }

    record MemberLoginResBody(
            MemberDto item,
            String apiKey,
            String accessToken
    ) {
    }

    @PostMapping("/login")
    @Transactional(readOnly = true)
    public RsData<MemberLoginResBody> login(
            @RequestBody @Valid MemberLoginReqBody reqBody
    ) {
        Member member = memberService
                .findByUsername(reqBody.username)
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 사용자입니다."));

        if (!member.matchPassword(reqBody.password))
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");

        String accessToken = authTokenService.genAccessToken(member);

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(member.getName()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        member.getApiKey(),
                        accessToken
                )
        );
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public MemberDto me() {
        Member member = rq.getActor();

        return new MemberDto(member);
    }

    // 테스트용, 임시 함수
    // 이 함수가 실행되려면 로그인이 되어있어야 한다.
    // 로그인이 잘 되었는지 확인하기 위해서 `SELECT * FROM MEMBER WHERE API_KEY = '로그인시_받은_API_KEY';` 가 수행된다.
    // 그런데 여기서는 회원정보를 활용하지 않는다.
    // 로그인 되었는지 확인하기 위해서  `SELECT * FROM MEMBER WHERE API_KEY = '로그인시_받은_API_KEY';` 가 수행되는 것은 살짝 무겁다는 생각이 든다.
    @GetMapping("/userKey1")
    public String userKey1() {
        return UUID.randomUUID().toString();
    }
}
