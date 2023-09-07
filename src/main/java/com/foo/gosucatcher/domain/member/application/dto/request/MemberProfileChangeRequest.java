package com.foo.gosucatcher.domain.member.application.dto.request;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.foo.gosucatcher.domain.member.domain.Member;

public record MemberProfileChangeRequest(
	@NotBlank(message = "이름은 비어있을 수 없습니다")
	@Length(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력 가능합니다")
	String name,
	@NotBlank(message = "비밀번호는 비어있을 수 없습니다")
	@Length(min = 5, max = 20, message = "비밀번호는 5자 이상 20자 이하로 입력 가능합니다")
	String password,
	String phoneNumber
) {

	public static Member toMember(MemberProfileChangeRequest memberInfoChangeRequest) {
		String changedName = memberInfoChangeRequest.name();
		String changedPassword = memberInfoChangeRequest.password();
		String changedPhoneNumber = memberInfoChangeRequest.phoneNumber();

		return Member.builder()
			.name(changedName)
			.password(changedPassword)
			.phoneNumber(changedPhoneNumber)
			.build();
	}
}
