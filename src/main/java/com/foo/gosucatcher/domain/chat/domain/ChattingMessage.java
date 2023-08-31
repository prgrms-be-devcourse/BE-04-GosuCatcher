package com.foo.gosucatcher.domain.chat.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

	//private Member member;

	//private ChattingRoom chattingRoom;

	private String senderNickName;

	private String message;
}
