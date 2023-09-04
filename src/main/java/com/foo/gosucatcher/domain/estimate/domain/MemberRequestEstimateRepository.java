package com.foo.gosucatcher.domain.estimate.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foo.gosucatcher.domain.member.domain.Member;

public interface MemberRequestEstimateRepository extends JpaRepository<MemberRequestEstimate, Long> {

	List<MemberRequestEstimate> findAllByMember(Member member);
}
