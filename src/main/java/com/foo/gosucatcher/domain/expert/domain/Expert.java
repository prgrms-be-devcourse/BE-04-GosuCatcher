package com.foo.gosucatcher.domain.expert.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "experts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expert extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private String storeName;

	private String location;

	private int maxTravelDistance;

	private String description;

	private boolean isBaroEstimate;

	@Builder
	public Expert(Member member, String storeName, String location, int maxTravelDistance, String description) {
		this.member = member;
		this.storeName = storeName;
		this.location = location;
		this.maxTravelDistance = maxTravelDistance;
		this.description = description;
		this.isBaroEstimate = false;
	}

	public void updateIsBaroEstimate(boolean isBaroEstimate) {
		this.isBaroEstimate = isBaroEstimate;
	}

	public void updateExpert(Expert updatedExpert) {
		this.storeName = updatedExpert.getStoreName();
		this.location = updatedExpert.getLocation();
		this.maxTravelDistance = updatedExpert.getMaxTravelDistance();
		this.description = updatedExpert.getDescription();
	}
}
