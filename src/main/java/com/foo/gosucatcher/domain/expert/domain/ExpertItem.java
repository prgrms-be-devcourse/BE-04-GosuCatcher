package com.foo.gosucatcher.domain.expert.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "expert_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpertItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@ManyToOne
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	@Builder
	public ExpertItem(Expert expert, SubItem subItem) {
		this.expert = expert;
		this.subItem = subItem;
	}
}
