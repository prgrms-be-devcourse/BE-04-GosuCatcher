package com.foo.gosucatcher.domain.chat.application;

import com.foo.gosucatcher.domain.chat.application.dto.request.ChattingMessageRequest;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingMessageResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingMessagesResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingMessage;
import com.foo.gosucatcher.domain.chat.domain.ChattingMessageRepository;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ChattingMessageService {

    private final ChattingMessageRepository chattingMessageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final MemberRepository memberRepository;

    public ChattingMessageResponse create(Long senderId, Long chattingRoomId, String message) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_CHATTING_ROOM));

        ChattingMessage chattingMessage = ChattingMessageRequest.toChattingMessage(sender, chattingRoom, message);
        chattingMessageRepository.save(chattingMessage);

        return ChattingMessageResponse.from(chattingMessage);
    }

    @Transactional(readOnly = true)
    public ChattingMessagesResponse findAllByChattingRoomId(Long chattingRoomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_CHATTING_ROOM));

        List<ChattingMessage> chattingMessages = chattingMessageRepository.findAllByChattingRoom(chattingRoom);

        return ChattingMessagesResponse.from(chattingMessages);
    }

    public ChattingMessagesResponse sendExpertEstimateMessage(List<ChattingRoomResponse> chattingRoomResponses, List<ExpertAutoEstimateResponse> expertAutoEstimateResponses) {
        if (chattingRoomResponses.size() != expertAutoEstimateResponses.size()) {
            throw new BusinessException(ErrorCode.CHATTING_ROOM_ASSIGNMENT_FAILED);
        }

        List<ChattingMessageResponse> chattingMessageResponses = new ArrayList<>();

        int chattingRoomCount = chattingRoomResponses.size();

        for (int count = 0; count < chattingRoomCount; count++) {
            ChattingMessageResponse chattingMessageResponse = create(expertAutoEstimateResponses.get(count).expert().id(), chattingRoomResponses.get(count).id(), expertAutoEstimateResponses.get(count).description());
            chattingMessageResponses.add(chattingMessageResponse);
        }

        return ChattingMessagesResponse.valueOf(chattingMessageResponses);
    }
}
