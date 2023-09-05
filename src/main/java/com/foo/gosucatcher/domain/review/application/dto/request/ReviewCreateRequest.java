package com.foo.gosucatcher.domain.review.application.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.review.domain.Review;

public record ReviewCreateRequest(
		@NotNull(message = "리뷰를 작성하는 사용자 ID를 입력해주세요")
		Long writerId,

		@NotBlank(message = "리뷰를 입력해주세요")
		@Length(min = 10, max = 600, message = "10자 이상 600자 이하로 입력 가능합니다")
		String description,

		@Min(value = 1, message = "별점은 1점 이상만 입력 가능합니다")
		@Max(value = 5, message = "별점은 5점 이하만 입력 가능합니다")
		int rating
) {

	public static Review toReview(ReviewCreateRequest reviewCreateRequest, Expert expert, Member writer,
			SubItem subItem) {
		return Review.builder()
				.expert(expert)
				.member(writer)
				.subItem(subItem)
				.description(reviewCreateRequest.description())
				.rating(reviewCreateRequest.rating())
				.build();
	}
}
