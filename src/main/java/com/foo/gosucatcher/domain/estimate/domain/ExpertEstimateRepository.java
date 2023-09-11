package com.foo.gosucatcher.domain.estimate.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;

public interface ExpertEstimateRepository extends JpaRepository<ExpertEstimate, Long> {

	@Query("SELECT ee FROM ExpertEstimate ee JOIN FETCH ee.expert JOIN FETCH ee.memberEstimate")
	List<ExpertEstimate> findAllWithFetchJoin();

	boolean existsByExpertAndSubItemAndMemberEstimateIsNull(Expert expert, SubItem subItem);

	@Query("SELECT m FROM ExpertEstimate m WHERE m.subItem.id = :subItemId AND m.activityLocation LIKE %:activityLocation%")
	List<ExpertEstimate> findAllBySubItemIdAndLocation(@Param("subItemId") Long subItemId, @Param("activityLocation") String activityLocation);
}
