package com.foo.gosucatcher.domain.member.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	boolean existsByEmail(String email);

	@Query("""
		SELECT
			CASE WHEN COUNT(m) > 0
				THEN true ELSE false
			END FROM Member m
		WHERE m.id = :memberId AND m.refreshToken = :refreshToken
		""")
	boolean existsByMemberIdAndRefreshToken(Long memberId, String refreshToken);
}
