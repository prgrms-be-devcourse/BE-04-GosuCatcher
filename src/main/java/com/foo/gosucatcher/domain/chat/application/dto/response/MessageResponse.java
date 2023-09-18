package com.foo.gosucatcher.domain.chat.application.dto.response;

import com.foo.gosucatcher.domain.chat.domain.Message;

public record MessageResponse(
	Long id,
	Long senderId,
	ChattingRoomResponse chattingRoomResponse,
	String content
) {

	public static MessageResponse from(Message message) {

		return new MessageResponse(
			message.getId(),
			message.getSender().getId(),
			ChattingRoomResponse.from(message.getChattingRoom()),
			message.getContent()
		);
	}
}
