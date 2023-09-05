package com.foo.gosucatcher.domain.item.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubItemRepository extends JpaRepository<SubItem, Long> {

	Optional<SubItem> findByName(String name);
}
