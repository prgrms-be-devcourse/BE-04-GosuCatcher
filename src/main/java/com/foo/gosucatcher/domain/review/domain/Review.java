package com.foo.gosucatcher.domain.review.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "isDeleted = false")
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
