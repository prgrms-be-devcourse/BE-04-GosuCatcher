package com.foo.gosucatcher.domain.likes.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {

	Optional<Likes> findByMemberIdAndExpertId(Long memberId, Long expertId);
}
