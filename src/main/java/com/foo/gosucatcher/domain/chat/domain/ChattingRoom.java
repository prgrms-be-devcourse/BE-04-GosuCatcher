package com.foo.gosucatcher.domain.chat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.foo.gosucatcher.global.BaseEntity;

import lombok.Getter;

@Getter
@Entity
@Table(name = "chatting_rooms")
public class ChattingRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long chattingRoomId;

	private Long expertResponseEstimateId;
}
