package com.foo.gosucatcher.domain.bucket.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BucketRepository extends JpaRepository<Bucket, Long> {

	Optional<Bucket> findByMemberIdAndExpertId(Long memberId, Long expertId);

	Slice<Bucket> findAllByMemberId(Long memberId, Pageable pageable);

	Page<Bucket> findAll(Pageable pageable);
}
