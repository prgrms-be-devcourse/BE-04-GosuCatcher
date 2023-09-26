package com.foo.gosucatcher.domain.chat.application.dto.response;

import com.foo.gosucatcher.domain.chat.domain.Message;

import java.util.List;

public record MessagesResponse(
        List<MessageResponse> messagesResponse
) {

    public static MessagesResponse from(List<Message> messages) {

        return new MessagesResponse(messages.stream()
                .map(MessageResponse::from)
                .toList());
    }

    public static MessagesResponse valueOf(List<MessageResponse> messageResponses) {

        return new MessagesResponse(messageResponses);
    }
}
