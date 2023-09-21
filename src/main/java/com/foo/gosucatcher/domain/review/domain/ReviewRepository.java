package com.foo.gosucatcher.domain.review.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

	Optional<Review> findById(Long id);

	@Query("select r from Review r where r.expert.id = ?1 and (?2 IS NULL or r.subItem.id = ?2) order by r.createdAt")
	Slice<Review> findAllByExpertIdAndSubItemIdOrderByCreatedAt(@Param("expertId")Long expertId, @Param("subItemId") Long subItemId, Pageable pageable);

	Long countByExpertId(Long expertId);
}
