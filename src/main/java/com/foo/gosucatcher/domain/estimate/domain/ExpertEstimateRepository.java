package com.foo.gosucatcher.domain.estimate.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;

public interface ExpertEstimateRepository extends JpaRepository<ExpertEstimate, Long> {

	boolean existsByExpertAndMemberRequestEstimate(Expert expert, MemberRequestEstimate memberRequestEstimate);

	boolean existsByExpertAndSubItem(Expert expert, SubItem subItem);
}
