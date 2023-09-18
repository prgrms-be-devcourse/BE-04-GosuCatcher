package com.foo.gosucatcher.domain.expert.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public interface ExpertItemRepository extends JpaRepository<ExpertItem, Long> {

	Optional<ExpertItem> findByExpertAndSubItem(Expert expert, SubItem subItem);

	@Query("SELECT COUNT(ei) > 0 FROM ExpertItem ei WHERE ei.expert.id = :expertId AND ei.subItem.id = :subItemId")
	boolean existsByExpertIdAndSubItemId(Long expertId, Long subItemId);
}
