package com.foo.gosucatcher.domain.review.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findAllByExpertId(Long expertId);
}
