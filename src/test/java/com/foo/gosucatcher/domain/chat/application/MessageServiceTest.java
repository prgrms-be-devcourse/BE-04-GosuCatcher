package com.foo.gosucatcher.domain.chat.application;

import com.foo.gosucatcher.domain.chat.application.dto.response.MessageResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessagesResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingStatus;
import com.foo.gosucatcher.domain.chat.domain.Message;
import com.foo.gosucatcher.domain.chat.domain.MessageRepository;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChattingRoomRepository chattingRoomRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MessageService messageService;

    private Member member;
    private MainItem mainItem;
    private SubItem subItem;
    private MemberEstimate memberEstimate;
    private ChattingRoom chattingRoom;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .name("성이름")
                .password("abcd11@@")
                .email("abcd123@abc.com")
                .phoneNumber("010-0000-0000")
                .build();

        mainItem = MainItem.builder().name("메인 서비스 이름").description("메인 서비스 설명").build();

        subItem = SubItem.builder().mainItem(mainItem).name("세부 서비스 이름").description("세부 서비스 설명").build();

        memberEstimate = MemberEstimate.builder()
                .member(member)
                .subItem(subItem)
                .location("서울 강남구 개포1동")
                .preferredStartDate(LocalDateTime.now().plusDays(3))
                .detailedDescription("추가 내용")
                .build();

        chattingRoom = ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();
    }

    @DisplayName("채팅 메시지 생성 테스트")
    @Test
    void create() {
        //given
        Long senderId = 1L;
        Long chattingRoomId = 2L;
        String content = "요청서에 대한 견적서입니다.";

        Message message = Message.builder()
                .sender(member)
                .chattingRoom(chattingRoom)
                .content(content)
                .build();

        when(memberRepository.findById(senderId)).thenReturn(Optional.of(member));
        when(chattingRoomRepository.findById(chattingRoomId)).thenReturn(Optional.of(chattingRoom));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageRepository.findById(null)).thenReturn(Optional.ofNullable(message));

        //when
        MessageResponse messageResponse = messageService.create(senderId, chattingRoomId, content, ChattingStatus.ENTER);
        Message result = messageRepository.findById(messageResponse.id()).get();

        //then
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getSender().getName()).isEqualTo(member.getName());
        assertThat(result.getChattingRoom().getMemberEstimate().getDetailedDescription()).isEqualTo(memberEstimate.getDetailedDescription());
    }

    @DisplayName("특정 채팅방의 모든 메시지를 조회하는 테스트")
    @Test
    void findAllByChattingRoomId() {
        //given
        Long chattingRoomId = 1L;
        String content = "요청서에 대한 견적서입니다.";

        Message message = Message.builder()
                .sender(member)
                .chattingRoom(chattingRoom)
                .content(content)
                .build();

        List<Message> messages = List.of(message);

        when(chattingRoomRepository.findById(chattingRoomId)).thenReturn(Optional.of(chattingRoom));
        when(messageRepository.findAllByChattingRoom(chattingRoom)).thenReturn(messages);

        //when
        MessagesResponse messagesResponse = messageService.findAllByChattingRoomId(chattingRoomId);

        //then
        assertThat(messagesResponse).isNotNull();
        assertThat(messagesResponse.messagesResponse().get(0).content()).isEqualTo("요청서에 대한 견적서입니다.");
    }

    @DisplayName("채팅 메시지를 삭제하는 테스트")
    @Test
    void delete() {
        //given
        Long messageId = 1L;
        String content = "요청서에 대한 견적서입니다.";

        Message message = Message.builder()
                .sender(member)
                .chattingRoom(chattingRoom)
                .content(content)
                .build();

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        //when
        messageService.delete(messageId);

        //then
        verify(messageRepository, times(1)).delete(message);
    }
}
