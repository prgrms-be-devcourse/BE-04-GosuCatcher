package com.foo.gosucatcher.domain.chat.application.dto.request;

import com.foo.gosucatcher.domain.chat.domain.Message;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingStatus;
import com.foo.gosucatcher.domain.member.domain.Member;

public record MessageRequest(
	ChattingStatus chattingStatus,
	Long senderId,
	Long chattingRoomId,
	String content
) {

	public static Message toMessage(ChattingStatus chattingStatus, Member sender, ChattingRoom chattingRoom, String content) {

		return Message.builder()
			.chattingStatus(chattingStatus)
			.sender(sender)
			.chattingRoom(chattingRoom)
			.content(content)
			.build();
	}
}
