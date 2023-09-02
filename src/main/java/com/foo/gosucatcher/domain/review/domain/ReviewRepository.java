package com.foo.gosucatcher.domain.review.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	Optional<Review> findReviewsByExpertId(Long expertId);
}
