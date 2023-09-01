package com.foo.gosucatcher.domain.review.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@OneToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	private String description;

	private int rating;

	private boolean isDeleted;

	@Builder
	public Review(Expert expert, Member member, SubItem subItem, String description, int rating) {
		this.expert = expert;
		this.member = member;
		this.subItem = subItem;
		this.description = description;
		this.rating = rating;
		this.isDeleted = false;
	}
}
