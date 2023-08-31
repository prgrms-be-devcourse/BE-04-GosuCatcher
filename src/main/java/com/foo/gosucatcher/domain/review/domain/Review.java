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

	private Long revieweeId;

	private Long reviewerId;

	private Long subServiceId;

	private String description;

	private double rating;

	private boolean isDeleted;

	@Builder
	public Review(String description, double rating) {
		this.description = description;
		this.rating = rating;
		this.isDeleted = false;
	}
}
