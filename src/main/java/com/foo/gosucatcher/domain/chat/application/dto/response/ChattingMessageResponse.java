package com.foo.gosucatcher.domain.chat.application.dto.response;

import com.foo.gosucatcher.domain.chat.domain.ChattingMessage;

public record ChattingMessageResponse(
        Long id,
        Long senderId,
        ChattingRoomResponse chattingRoomResponse,
        String message
) {

    public static ChattingMessageResponse from(ChattingMessage chattingMessage) {

        return new ChattingMessageResponse(chattingMessage.getId(), chattingMessage.getSender().getId(), ChattingRoomResponse.from(chattingMessage.getChattingRoom()), chattingMessage.getMessage());
    }
}
