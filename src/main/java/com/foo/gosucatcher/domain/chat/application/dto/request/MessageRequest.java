package com.foo.gosucatcher.domain.chat.application.dto.request;

import com.foo.gosucatcher.domain.chat.domain.Message;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.MessageType;
import com.foo.gosucatcher.domain.member.domain.Member;

public record MessageRequest(
	MessageType type,
	Long senderId,
	Long chattingRoomId,
	String content
) {

	public static Message toMessage(MessageType type, Member sender, ChattingRoom chattingRoom, String content) {

		return Message.builder()
			.messageType(type)
			.sender(sender)
			.chattingRoom(chattingRoom)
			.content(content)
			.build();
	}
}
