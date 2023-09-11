package com.foo.gosucatcher.domain.chat.application.dto.response;

import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;

import java.util.List;

public record ChattingRoomsResponse(
        List<ChattingRoomResponse> chattingRoomsResponse
) {

    public static ChattingRoomsResponse from(List<ChattingRoom> chattingRooms) {

        return new ChattingRoomsResponse(chattingRooms.stream()
                .map(ChattingRoomResponse::from)
                .toList());
    }
}
