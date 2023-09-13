package com.foo.gosucatcher.domain.item.domain;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubItemRepository extends JpaRepository<SubItem, Long> {

	Optional<SubItem> findByName(String name);

	Slice<SubItem> findAllByMainItemName(String name, Pageable pageable);
}
