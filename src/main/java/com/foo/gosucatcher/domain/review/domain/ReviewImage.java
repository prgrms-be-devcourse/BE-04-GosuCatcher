package com.foo.gosucatcher.domain.review.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "review_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;

	private String path;

	@Builder
	public ReviewImage(Review review, String path) {
		this.review = review;
		this.path = path;
	}

	public static ReviewImage of(Review review, String path) {
		return ReviewImage.builder()
			.review(review)
			.path(path)
			.build();
	}

	public void addReview(Review review) {
		this.review = review;
	}
}
