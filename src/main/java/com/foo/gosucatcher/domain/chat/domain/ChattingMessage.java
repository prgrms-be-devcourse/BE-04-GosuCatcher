package com.foo.gosucatcher.domain.chat.domain;

import javax.persistence.*;

import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "chatting_messages")
@SQLDelete(sql = "UPDATE chatting_messages SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingMessage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatting_room_id")
	private ChattingRoom chattingRoom;

	private String message;

	private boolean isDeleted = Boolean.FALSE;

	@Builder
	public ChattingMessage(Member sender, ChattingRoom chattingRoom, String message) {
		this.sender = sender;
		this.chattingRoom = chattingRoom;
		this.message = message;
	}
}
