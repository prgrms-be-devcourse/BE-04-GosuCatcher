package com.foo.gosucatcher.domain.estimate.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foo.gosucatcher.domain.member.domain.Member;

public interface MemberRequestEstimateRepository extends JpaRepository<MemberRequestEstimate, Long> {

	List<MemberRequestEstimate> findAllByMember(Member member);

	@Query("SELECT m FROM MemberRequestEstimate m WHERE m.member.id = :memberId AND m.subItem.id = :subItemId AND m.isClosed = false")
	List<MemberRequestEstimate> findByMemberIdAndSubItemIdAndIsNotClosed(@Param("memberId") Long memberId,
		@Param("subItemId") Long subItemId);
}
