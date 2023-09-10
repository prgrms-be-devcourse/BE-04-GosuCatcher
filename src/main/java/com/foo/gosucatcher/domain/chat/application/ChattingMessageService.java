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
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.foo.gosucatcher.global.error.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class ChattingMessageService {

    private final ChattingMessageRepository chattingMessageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final MemberRepository memberRepository;

    public ChattingMessageResponse create(Long senderId, Long chattingRoomId, String message) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

        ChattingMessage chattingMessage = ChattingMessageRequest.toChattingMessage(sender, chattingRoom, message);
        ChattingMessage savedChattingMessage = chattingMessageRepository.save(chattingMessage);

        return ChattingMessageResponse.from(savedChattingMessage);
    }

    @Transactional(readOnly = true)
    public ChattingMessagesResponse findAllByChattingRoomId(Long chattingRoomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

        List<ChattingMessage> chattingMessages = chattingMessageRepository.findAllByChattingRoom(chattingRoom);

        return ChattingMessagesResponse.from(chattingMessages);
    }

    public void delete(Long chattingMessageId) {
        ChattingMessage chattingMessage = chattingMessageRepository.findById(chattingMessageId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_MESSAGE));

        chattingMessageRepository.delete(chattingMessage);
    }

    public ChattingMessagesResponse sendExpertEstimateMessage(List<ChattingRoomResponse> chattingRoomResponses, List<ExpertAutoEstimateResponse> expertAutoEstimateResponses) {
        if (chattingRoomResponses.size() != expertAutoEstimateResponses.size()) {
            throw new BusinessException(CHATTING_ROOM_ASSIGNMENT_FAILED);
        }

        List<ChattingMessageResponse> chattingMessageResponses = IntStream.range(0, chattingRoomResponses.size())
                .mapToObj(count -> create(expertAutoEstimateResponses.get(count).expert().id(), chattingRoomResponses.get(count).id(), expertAutoEstimateResponses.get(count).description()))
                .collect(Collectors.toList());

        return ChattingMessagesResponse.valueOf(chattingMessageResponses);
    }
}
