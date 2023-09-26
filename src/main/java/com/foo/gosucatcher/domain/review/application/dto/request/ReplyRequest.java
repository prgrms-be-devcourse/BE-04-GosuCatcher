package com.foo.gosucatcher.domain.review.application.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.foo.gosucatcher.domain.review.domain.Reply;
import com.foo.gosucatcher.domain.review.domain.Review;

public record ReplyRequest(

	@NotBlank(message = "리뷰에 대한 답글을 입력해주세요")
	@Length(min = 10, max = 600, message = "10자 이상 600자 이하로 입력 가능합니다")
	String content
) {

	public static Reply toReply(ReplyRequest replyRequest) {
		return Reply.builder()
			.content(replyRequest.content())
			.build();
	}

	public static Reply toReply(ReplyRequest replyRequest, Review parent) {
		return Reply.builder()
			.content(replyRequest.content())
			.parent(parent)
			.build();
	}
}
