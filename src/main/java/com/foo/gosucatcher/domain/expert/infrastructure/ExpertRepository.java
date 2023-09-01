package com.foo.gosucatcher.domain.expert.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foo.gosucatcher.domain.expert.domain.Expert;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
}
