package com.foo.gosucatcher.domain.chat.application;

import com.foo.gosucatcher.domain.chat.application.dto.request.MessageRequest;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessageResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessagesResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.domain.Message;
import com.foo.gosucatcher.domain.chat.domain.MessageRepository;
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
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChattingRoomRepository chattingRoomRepository;
    private final MemberRepository memberRepository;

    public MessageResponse create(Long senderId, Long chattingRoomId, String content) {
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

        Message message = MessageRequest.toMessage(sender, chattingRoom, content);
        Message savedMessage = messageRepository.save(message);

        return MessageResponse.from(savedMessage);
    }

    @Transactional(readOnly = true)
    public MessagesResponse findAllByChattingRoomId(Long chattingRoomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

        List<Message> messages = messageRepository.findAllByChattingRoom(chattingRoom);

        return MessagesResponse.from(messages);
    }

    public void delete(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MESSAGE));

        messageRepository.delete(message);
    }

    public MessagesResponse sendExpertEstimateMessage(List<ChattingRoomResponse> chattingRoomResponses, List<ExpertAutoEstimateResponse> expertAutoEstimateResponses) {
        if (chattingRoomResponses.size() != expertAutoEstimateResponses.size()) {
            throw new BusinessException(CHATTING_ROOM_ASSIGNMENT_FAILED);
        }

        List<MessageResponse> messageResponses = IntStream.range(0, chattingRoomResponses.size())
                .mapToObj(count -> create(expertAutoEstimateResponses.get(count).expert().id(), chattingRoomResponses.get(count).id(), expertAutoEstimateResponses.get(count).description()))
                .collect(Collectors.toList());

        return MessagesResponse.valueOf(messageResponses);
    }
}
