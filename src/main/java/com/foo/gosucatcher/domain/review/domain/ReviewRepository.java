package com.foo.gosucatcher.domain.review.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findAll(Pageable pageable);

	Slice<Review> findAllByExpertId(Long expertId, Pageable pageable);

	Optional<Review> findById(Long id);

	Slice<Review> findAllByExpertIdAndSubItemId(Long expertId, Long subItemId, Pageable pageable);

	Long countByExpertId(Long expertId);
}
