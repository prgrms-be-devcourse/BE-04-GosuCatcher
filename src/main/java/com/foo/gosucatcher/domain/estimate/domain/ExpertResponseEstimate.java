package com.foo.gosucatcher.domain.estimate.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "expert_response_estimates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpertResponseEstimate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@ManyToOne
	@JoinColumn(name = "member_request_estimate_id")
	private MemberRequestEstimate memberRequestEstimate;

	private int cost;

	private String description;

	@Builder
	public ExpertResponseEstimate(Expert expert, MemberRequestEstimate memberRequestEstimate, int cost, String description) {
		this.expert = expert;
		this.memberRequestEstimate = memberRequestEstimate;
		this.cost = cost;
		this.description = description;
	}
}
