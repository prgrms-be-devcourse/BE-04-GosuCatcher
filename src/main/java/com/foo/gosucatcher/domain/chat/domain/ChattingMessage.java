package com.foo.gosucatcher.domain.chat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chatting_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingMessage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member sender;

	@ManyToOne
	@JoinColumn(name = "chatting_room_id")
	private ChattingRoom chattingRoom;

	private String message;

	public ChattingMessage(Member sender, ChattingRoom chattingRoom, String message) {
		this.sender = sender;
		this.chattingRoom = chattingRoom;
		this.message = message;
	}
}
