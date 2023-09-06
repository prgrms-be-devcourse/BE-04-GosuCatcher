package com.foo.gosucatcher.domain.review.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	Slice<Review> findAllByExpertId(Long expertId, Pageable pageable);

	Optional<Review> findById(Long id);
}
