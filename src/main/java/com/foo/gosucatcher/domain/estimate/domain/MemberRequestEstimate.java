package com.foo.gosucatcher.domain.estimate.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member_request_estimates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequestEstimate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long memberId;

	private Long subItemId;

	private String requiredLocation;
}
