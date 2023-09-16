package com.foo.gosucatcher.domain.chat.application.dto.request;

import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.Message;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertNormalEstimateResponse;
import com.foo.gosucatcher.domain.member.domain.Member;

public record MessageRequest(
	Long senderId,
	Long chattingRoomId,
	String content
) {

	public static Message toMessage(Member sender, ChattingRoom chattingRoom, String content) {

		return Message.builder()
			.sender(sender)
			.chattingRoom(chattingRoom)
			.content(content)
			.build();
	}

	public static MessageRequest from(ChattingRoomResponse chattingRoomResponse, ExpertNormalEstimateResponse expertNormalEstimateResponse) {
		return new MessageRequest(expertNormalEstimateResponse.id(),
			chattingRoomResponse.id(),
			expertNormalEstimateResponse.description());
	}
}
