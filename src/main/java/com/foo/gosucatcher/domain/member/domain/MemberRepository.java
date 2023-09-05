package com.foo.gosucatcher.domain.member.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
  
	Optional<Member> findByEmail(String email);
}
