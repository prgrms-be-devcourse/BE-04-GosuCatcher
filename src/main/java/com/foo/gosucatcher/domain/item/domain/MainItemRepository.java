package com.foo.gosucatcher.domain.item.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MainItemRepository extends JpaRepository<MainItem, Long> {

	Optional<MainItem> findByName(String name);
}
