package com.foo.gosucatcher.domain.item.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubItemRepository extends JpaRepository<SubItem, Long> {

	Optional<SubItem> findByName(String name);

	Slice<SubItem> findAllByMainItemName(String name, Pageable pageable);

	@Query("SELECT si FROM SubItem si JOIN FETCH si.mainItem WHERE si.name LIKE %:keyword%")
	List<SubItem> findByNameContains(@Param("keyword") String keyword);
}
