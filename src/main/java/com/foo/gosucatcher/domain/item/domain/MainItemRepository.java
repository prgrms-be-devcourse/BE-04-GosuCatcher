package com.foo.gosucatcher.domain.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MainItemRepository extends JpaRepository<MainItem, Long> {

    Optional<MainItem> findByName(String name);
}
