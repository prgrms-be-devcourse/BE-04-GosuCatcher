package com.foo.gosucatcher.domain.item.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "sub_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "main_item_id")
	private MainItem mainItem;

	private String name;

	private String description;

	@Builder
	public SubItem(MainItem mainItem, String name, String description) {
		this.mainItem = mainItem;
		this.name = name;
		this.description = description;
	}

	public void update(SubItem subItem) {
		this.name = subItem.getName();
		this.description = subItem.getDescription();
	}
}
