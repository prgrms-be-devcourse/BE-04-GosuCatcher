package com.foo.gosucatcher.domain.expert.domain;

import javax.persistence.Entity;
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

	@OneToOne
	@JoinColumn(name = "member_id")
	private Member member;

	private String storeName;

	private String location;

	private int distance;

	private String description;

	private boolean isBaroEstimate;

	@Builder
	public Expert(Member member, String storeName, String location, int distance, String description) {
		this.member = member;
		this.storeName = storeName;
		this.location = location;
		this.distance = distance;
		this.description = description;
		this.isBaroEstimate = false;
	}
}
