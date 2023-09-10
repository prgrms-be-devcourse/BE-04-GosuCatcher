package com.foo.gosucatcher.domain.chat.application.dto.request;

import com.foo.gosucatcher.domain.chat.domain.ChattingMessage;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.member.domain.Member;

public record ChattingMessageRequest(
        Long senderId,
        Long chattingRoomId,
        String message
) {

    public static ChattingMessage toChattingMessage(Member sender, ChattingRoom chattingRoom, String message) {

        return ChattingMessage.builder()
                .sender(sender)
                .chattingRoom(chattingRoom)
                .message(message)
                .build();
    }
}
