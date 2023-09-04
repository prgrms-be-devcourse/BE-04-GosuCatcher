package com.foo.gosucatcher.domain.member.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String password;

	private String email;

	private String phoneNumber;

	@Embedded
	private ImageFile profileImageFile;

	private boolean isDeleted;

	@Builder
	public Member(String name, String password, String email, String phoneNumber, ImageFile profileImageFile) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.profileImageFile = profileImageFile;
		this.isDeleted = false;
	}

	public boolean logIn(String password) {
		return this.password.equals(password);
	}

	public void deleteMember() {
		this.isDeleted = true;
	}

	public void changeMemberInfo(MemberInfoChangeRequest memberInfoChangeRequest) {
		this.name = memberInfoChangeRequest.name();
		this.password = memberInfoChangeRequest.password();
		this.phoneNumber = memberInfoChangeRequest.phoneNumber();
	}

	public void changeProfileImageFile(ImageFile profileImageFile) {
		if (profileImageFile == null) {
			//todo: 예외클래스 리팩토링 필요
			throw new InvalidValueException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		this.profileImageFile = profileImageFile;
	}
}
