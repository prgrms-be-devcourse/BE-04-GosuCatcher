package com.foo.gosucatcher.domain.estimate.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foo.gosucatcher.domain.member.domain.Member;

public interface MemberEstimateRepository extends JpaRepository<MemberEstimate, Long> {

	List<MemberEstimate> findAllByMember(Member member);

	@Query("SELECT m FROM MemberEstimate m WHERE m.member.id = :memberId AND m.subItem.id = :subItemId AND m.isClosed = false")
	List<MemberEstimate> findByMemberIdAndSubItemIdAndIsNotClosed(@Param("memberId") Long memberId,
		@Param("subItemId") Long subItemId);

	@Query("SELECT m FROM MemberEstimate m WHERE m.status = 'PENDING' AND m.expert.id = :expertId")
	List<MemberEstimate> findAllByPendingAndExpertId(@Param("expertId") Long expertId);
}
