package com.foo.gosucatcher.domain.estimate.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foo.gosucatcher.domain.member.domain.Member;

@Repository
public interface MemberRequestEstimateRepository extends JpaRepository<MemberRequestEstimate, Long> {

	List<MemberRequestEstimate> findAllByMember(Member member);
}
