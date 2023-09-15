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
@Table(name = "messages")
@SQLDelete(sql = "UPDATE messages SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private Member sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatting_room_id")
	private ChattingRoom chattingRoom;

	private String content;

	@Enumerated(EnumType.STRING)
	private ChattingStatus chattingStatus;

	private boolean isDeleted = Boolean.FALSE;

	@Builder
	public Message(Member sender, ChattingRoom chattingRoom, String content, ChattingStatus chattingStatus) {
		this.sender = sender;
		this.chattingRoom = chattingRoom;
		this.content = content;
		this.chattingStatus = chattingStatus;
	}
}
