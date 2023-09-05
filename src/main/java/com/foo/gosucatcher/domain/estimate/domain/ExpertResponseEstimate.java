package com.foo.gosucatcher.domain.estimate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_request_estimate_id")
	private MemberRequestEstimate memberRequestEstimate;

	private int totalCost;

	@Column(nullable = false)
	private String description;

	private boolean isOftenUsed;

	@Builder
	public ExpertResponseEstimate(Expert expert, MemberRequestEstimate memberRequestEstimate, int totalCost,
		String description, boolean isOftenUsed) {
		this.expert = expert;
		this.memberRequestEstimate = memberRequestEstimate;
		this.totalCost = checkInvalidTotalCost(totalCost);
		this.description = description;
		this.isOftenUsed = isOftenUsed;
	}

	public void update(ExpertResponseEstimate expertResponseEstimate) {
		this.expert = expertResponseEstimate.getExpert();
		this.memberRequestEstimate = expertResponseEstimate.getMemberRequestEstimate();
		this.totalCost = checkInvalidTotalCost(expertResponseEstimate.getTotalCost());
		this.description = expertResponseEstimate.getDescription();
	}

	private int checkInvalidTotalCost(int totalCost) {
		if (totalCost <= 0) {
			throw new BusinessException(ErrorCode.TOTAL_AMOUNT_CANNOT_BE_LESS_THAN_ZERO);
		}

		return totalCost;
	}
}
