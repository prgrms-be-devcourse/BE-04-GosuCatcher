package com.foo.gosucatcher.domain.member.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 20, nullable = false)
	private String name;

	@Column(length = 20, nullable = false)
	private String password;

	@Column(length = 50, nullable = false, unique = true)
	private String email;

	@Column(length = 12, unique = true)
	private String phoneNumber;

	@Embedded
	@Column(nullable = false)
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

	public void changeMemberInfo(Member member) {
		this.name = member.getName();
		this.password = member.getPassword();
		this.phoneNumber = member.getPhoneNumber();
	}

	public void changeProfileImageFile(ImageFile profileImageFile) {
		if (profileImageFile == null) {
			throw new BusinessException(ErrorCode.INVALID_IMAGE);
		}

		this.profileImageFile = profileImageFile;
	}
}
