package com.foo.gosucatcher.domain.expert.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertRepository extends JpaRepository<Expert, Long> {

	Optional<Expert> findByStoreName(String storeName);

	List<Expert> findAllByIsDeletedFalse();
}
