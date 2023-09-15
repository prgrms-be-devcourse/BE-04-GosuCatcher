package com.foo.gosucatcher.domain.expert.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpertImageRepository extends JpaRepository<ExpertImage, Long> {

	Optional<ExpertImage> findByFilename(String filename);

	List<ExpertImage> findAllByExpert(Expert expert);

	Optional<ExpertImage> findByFilenameAndExpert(String filename, Expert expert);
}
