package com.foo.gosucatcher.domain.bucket.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketRepository extends JpaRepository<Bucket, Long> {

	Optional<Bucket> findByMemberIdAndExpertId(Long memberId, Long expertId);
}
