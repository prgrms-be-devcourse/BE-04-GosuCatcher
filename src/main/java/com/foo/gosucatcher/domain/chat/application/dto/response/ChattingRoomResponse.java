package com.foo.gosucatcher.domain.chat.application.dto.response;

import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;

public record ChattingRoomResponse(
	Long id,
	MemberEstimateResponse memberEstimateResponse
) {

	public static ChattingRoomResponse from(ChattingRoom chattingRoom) {

		return new ChattingRoomResponse(chattingRoom.getId(), MemberEstimateResponse.from(chattingRoom.getMemberEstimate()));
	}
}
