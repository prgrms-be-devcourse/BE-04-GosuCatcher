package com.foo.gosucatcher.domain.estimate.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "expert_estimates")
@SQLDelete(sql = "UPDATE expert_estimates SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpertEstimate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_request_estimate_id")
	private MemberEstimate memberEstimate;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	private int totalCost;

	private String activityLocation;

	@Column(nullable = false)
	private String description;

	private boolean isDeleted = Boolean.FALSE;

	@Builder

	public ExpertEstimate(Expert expert, MemberEstimate memberEstimate, SubItem subItem, int totalCost,
						  String activityLocation, String description) {
		this.expert = expert;
		this.memberEstimate = memberEstimate;
		this.subItem = subItem;
		this.totalCost = checkInvalidTotalCost(totalCost);
		this.activityLocation = activityLocation;
		this.description = description;
	}

	public void addSubItem(SubItem subItem) {
		this.subItem = subItem;
	}

	public void addMemberRequest(MemberEstimate memberRequestEstimate) {
		this.memberEstimate = memberRequestEstimate;
	}

	public boolean isAuto() {
		return memberEstimate == null;
	}

	public void update(ExpertEstimate expertEstimate) {
		this.expert = expertEstimate.getExpert();
		this.memberEstimate = expertEstimate.getMemberEstimate();
		this.subItem = expertEstimate.getSubItem();
		this.totalCost = checkInvalidTotalCost(expertEstimate.getTotalCost());
		this.activityLocation = expertEstimate.getActivityLocation();
		this.description = expertEstimate.getDescription();

	}

	private int checkInvalidTotalCost(int totalCost) {
		if (totalCost <= 0) {
			throw new BusinessException(ErrorCode.TOTAL_AMOUNT_CANNOT_BE_LESS_THAN_ZERO);
		}
		return totalCost;
	}
}
