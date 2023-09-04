package com.foo.gosucatcher.domain.likes.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.NotSupportedLikesException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Expert expert;

	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Builder
	public Likes(Expert expert, Member member) {
		if (member.getId().equals(expert.getMember().getId())) {
			throw new NotSupportedLikesException(ErrorCode.NOT_SUPPORTED_SELF_LIKES);
		}
		this.expert = expert;
		this.member = member;
	}
}
