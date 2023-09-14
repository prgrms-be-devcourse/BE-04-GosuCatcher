package com.foo.gosucatcher.domain.expert.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foo.gosucatcher.domain.item.domain.SubItem;

public interface ExpertItemRepository extends JpaRepository<ExpertItem, Long> {

    Optional<ExpertItem> findByExpertAndSubItem(Expert expert, SubItem subItem);
}
