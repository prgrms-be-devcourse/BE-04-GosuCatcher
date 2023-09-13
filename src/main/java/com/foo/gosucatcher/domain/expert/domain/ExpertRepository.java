package com.foo.gosucatcher.domain.expert.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

	Optional<Expert> findByStoreName(String storeName);

	Optional<Expert> findByMemberId(Long memberId);

	List<Expert> findAll();

	@Query("SELECT distinct e FROM Expert e" +
		" JOIN FETCH e.expertItemList ei" +
		" JOIN FETCH ei.subItem si" +
		" WHERE (si.name = :subItem OR :subItem IS NULL)" +
		" AND (e.location = :location OR :location IS NULL)")
	Slice<Expert> findBySubItemAndLocation(String subItem, String location, Pageable pageable);
  
  @Query("SELECT e FROM Expert e JOIN FETCH e.expertItemList ei JOIN FETCH ei.subItem WHERE e.id = :id")
	Optional<Expert> findExpertWithSubItemsById(@Param("id") Long id);
}