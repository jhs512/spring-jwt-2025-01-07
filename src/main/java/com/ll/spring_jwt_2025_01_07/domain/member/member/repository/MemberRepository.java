package com.ll.spring_jwt_2025_01_07.domain.member.member.repository;

import com.ll.spring_jwt_2025_01_07.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByApiKey(String apiKey);
}