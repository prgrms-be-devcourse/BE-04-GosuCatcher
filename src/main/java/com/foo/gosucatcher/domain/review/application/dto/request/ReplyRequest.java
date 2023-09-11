package com.foo.gosucatcher.domain.review.application.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.foo.gosucatcher.domain.review.domain.Reply;

public record ReplyRequest(

	@NotNull(message = "리뷰를 작성하는 고수의 ID를 입력해주세요")
	Long writerId,

	@NotBlank(message = "리뷰에 대한 답글을 입력해주세요")
	@Length(min = 10, max = 600, message = "10자 이상 600자 이하로 입력 가능합니다")
	String content
) {

	public static Reply toReply(ReplyRequest replyRequest) {
		return Reply.builder()
			.content(replyRequest.content())
			.build();
	}
}
