package com.foo.gosucatcher.domain.member.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foo.gosucatcher.domain.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
