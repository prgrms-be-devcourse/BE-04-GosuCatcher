package com.foo.gosucatcher.domain.chat.application.dto.response;

import com.foo.gosucatcher.domain.chat.domain.ChattingMessage;

import java.util.List;

public record ChattingMessagesResponse(
        List<ChattingMessageResponse> chattingMessagesResponse
) {

    public static ChattingMessagesResponse from(List<ChattingMessage> chattingMessages) {

        return new ChattingMessagesResponse(chattingMessages.stream()
                .map(ChattingMessageResponse::from)
                .toList());
    }
}
