package com.foo.gosucatcher.domain.estimate.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpertEstimateRepository extends JpaRepository<ExpertEstimate, Long> {

	@Query("SELECT m FROM ExpertEstimate m WHERE m.subItem.id = :subItemId AND m.activityLocation LIKE %:activityLocation% AND m.isAuto = true")
	List<ExpertEstimate> findAllBySubItemIdAndLocationAndIsAuto(@Param("subItemId") Long subItemId, @Param("activityLocation") String activityLocation);
}
