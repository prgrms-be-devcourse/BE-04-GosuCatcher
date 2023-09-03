package com.foo.gosucatcher.domain.member.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.global.BaseEntity;

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

	private String profileImagePath;

	private boolean isDeleted;

	@Builder
	public Member(String name, String password, String email, String phoneNumber, String profileImagePath) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.profileImagePath = profileImagePath;
		this.isDeleted = false;
	}

	public void logIn(String password) {
		if (!this.password.equals(password)) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}
	}

	public void deleteMember() {
		this.isDeleted = true;
	}

	public void changeMemberInfo(MemberInfoChangeRequest memberInfoChangeRequest) {
		this.name = memberInfoChangeRequest.name();
		this.password = memberInfoChangeRequest.password();
		this.phoneNumber = memberInfoChangeRequest.phoneNumber();
	}
}
