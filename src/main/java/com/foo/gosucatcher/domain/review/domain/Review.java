package com.foo.gosucatcher.domain.review.domain;

import static java.lang.Boolean.FALSE;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
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
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE reviews SET is_deleted = true WHERE id = ?")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JoinColumn(name = "parent_id")
	private Review parent;

	private String content;

	private int rating;

	private boolean isDeleted = FALSE;

	@Builder
	public Review(Expert expert, Member member, SubItem subItem, Review parent, String content, int rating) {
		this.expert = expert;
		this.member = member;
		this.subItem = subItem;
		this.parent = parent;
		this.content = content;
		this.rating = rating;
		this.isDeleted = false;
	}

	public void update(Review updatedReview) {
		content = updatedReview.getContent();
		rating = updatedReview.getRating();
	}

	public void updateParent(Review parent) {
		this.parent = parent;
	}

	boolean isReply() {
		return parent == null;
	}

	// public void updateReply(Reply reply) {
	// 	this.reply = reply;
	// }
}
