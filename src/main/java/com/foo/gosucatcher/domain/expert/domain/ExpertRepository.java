package com.foo.gosucatcher.domain.expert.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

	Optional<Expert> findByStoreName(String storeName);

	List<Expert> findAll();
  
  @Query("SELECT e FROM Expert e JOIN FETCH e.expertItemList ei JOIN FETCH ei.subItem WHERE e.id = :id")
	Optional<Expert> findExpertWithSubItemsById(@Param("id") Long id);
}
