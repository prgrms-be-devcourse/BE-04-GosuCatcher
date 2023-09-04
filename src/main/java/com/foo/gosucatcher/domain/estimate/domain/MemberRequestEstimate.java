package com.foo.gosucatcher.domain.estimate.domain;

import java.time.LocalDateTime;

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

import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;

import lombok.AccessLevel;
import lombok.Builder;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private LocalDateTime preferredStartDate;

	private String detailedDescription;

	@Builder
	public MemberRequestEstimate(Member member, SubItem subItem, String location, LocalDateTime preferredStartDate,
		String detailedDescription) {
		this.member = member;
		this.subItem = subItem;
		this.location = location;
		this.preferredStartDate = validatePreferredStartDate(preferredStartDate);
		this.detailedDescription = detailedDescription;
	}

	public void update(MemberRequestEstimate memberRequestEstimate) {
		this.location = memberRequestEstimate.getLocation();
		this.preferredStartDate = validatePreferredStartDate(memberRequestEstimate.getPreferredStartDate());
		this.detailedDescription = memberRequestEstimate.getDetailedDescription();
	}

	private LocalDateTime validatePreferredStartDate(LocalDateTime preferredStartDate) {
		if (LocalDateTime.now().isAfter(preferredStartDate)) {
			throw new BusinessException(ErrorCode.INVALID_START_DATE);
		}

		return preferredStartDate;
	}
}
