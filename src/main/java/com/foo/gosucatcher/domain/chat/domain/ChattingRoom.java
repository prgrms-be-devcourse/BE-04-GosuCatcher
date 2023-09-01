package com.foo.gosucatcher.domain.chat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chatting_rooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long expertResponseEstimateId;

	private boolean isDeleted;

	@Builder
	public ChattingRoom(Long expertResponseEstimateId) {
		this.expertResponseEstimateId = expertResponseEstimateId;
		this.isDeleted = false;
	}
}
