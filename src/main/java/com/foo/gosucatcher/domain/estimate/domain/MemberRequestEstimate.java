package com.foo.gosucatcher.domain.estimate.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne
	@JoinColumn(name = "sub_item_id")
	private SubItem subItem;

	@Column(nullable = false)
	private String location;

	@Column(nullable = false)
	private LocalDateTime startDate;

	private String detailedDescription;

	public void update(String location, LocalDateTime startDate, String detailedDescription) {
		this.location = location;
		this.startDate = startDate;
		this.detailedDescription = detailedDescription;
	}

	@Builder
	public MemberRequestEstimate(Member member, SubItem subItem, String location, LocalDateTime startDate,
		String detailedDescription) {
		this.member = member;
		this.subItem = subItem;
		this.location = location;
		this.startDate = startDate;
		this.detailedDescription = detailedDescription;
	}
}
