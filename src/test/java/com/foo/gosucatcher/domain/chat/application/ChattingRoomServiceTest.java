package com.foo.gosucatcher.domain.chat.application;

import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.item.domain.MainItem;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChattingRoomServiceTest {

    @Mock
    private MemberEstimateRepository memberEstimateRepository;

    @Mock
    private ChattingRoomRepository chattingRoomRepository;

    @InjectMocks
    private ChattingRoomService chattingRoomService;

    private Member member;
    private MainItem mainItem;
    private SubItem subItem;
    private MemberEstimate memberEstimate;

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
    }

    @DisplayName("채팅방 생성 테스트")
    @Test
    void create() {
        //given
        Long memberEstimateId = 1L;
        when(memberEstimateRepository.findById(memberEstimateId)).thenReturn(Optional.of(memberEstimate));

        ChattingRoom chattingRoom = ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();

        List<ChattingRoom> expectedChattingRooms = new ArrayList<>();
        expectedChattingRooms.add(chattingRoom);

        when(chattingRoomRepository.saveAll(any(Iterable.class))).thenReturn(expectedChattingRooms);

        //when
        ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.create(memberEstimateId);
        ChattingRoomResponse result = chattingRoomsResponse.chattingRoomsResponse().get(0);

        //then
        assertThat(result.memberEstimateResponse().location()).isEqualTo("서울 강남구 개포1동");
        assertThat(result.memberEstimateResponse().detailedDescription()).isEqualTo("추가 내용");
    }

    @DisplayName("모든 채팅방 조회 테스트")
    @Test
    void findAll() {
        //given
        List<ChattingRoom> expectedChattingRooms = new ArrayList<>();
        expectedChattingRooms.add(new ChattingRoom(memberEstimate));

        when(chattingRoomRepository.findAll()).thenReturn(expectedChattingRooms);

        //when
        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAll();
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(expectedChattingRooms.size()).isEqualTo(chattingRoomResponses.size());
    }

    @DisplayName("회원 요청서에 해당하는 모든 채팅방 조회 테스트")
    @Test
    void findAllByMemberEstimate() {
        //given
        Long memberEstimateId = 1L;
        ChattingRoom chattingRoom = ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();

        ChattingRoom chattingRoom2 = ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();

        when(memberEstimateRepository.findById(memberEstimateId)).thenReturn(Optional.of(memberEstimate));

        List<ChattingRoom> expectedChattingRooms = List.of(chattingRoom, chattingRoom2);

        when(chattingRoomRepository.findAllByMemberEstimate(memberEstimate)).thenReturn(expectedChattingRooms);

        //when
        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAllByMemberEstimate(memberEstimateId);
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(expectedChattingRooms.size()).isEqualTo(chattingRoomResponses.size());
    }

    @DisplayName("id로 채팅방 조회 테스트")
    @Test
    void findById() {
        //given
        Long chattingRoomId = 1L;
        ChattingRoom chattingRoom = ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();

        when(chattingRoomRepository.findById(chattingRoomId)).thenReturn(Optional.of(chattingRoom));

        //when
        ChattingRoomResponse chattingRoomResponse = chattingRoomService.findById(chattingRoomId);

        //then
        assertThat(chattingRoomResponse.memberEstimateResponse().detailedDescription()).isEqualTo(memberEstimate.getDetailedDescription());
        assertThat(chattingRoomResponse.memberEstimateResponse().location()).isEqualTo(memberEstimate.getLocation());
    }

    @DisplayName("채팅방 삭제 테스트")
    @Test
    void delete() {
        //given
        Long chattingRoomId = 1L;
        ChattingRoom chattingRoom = ChattingRoom.builder()
                .memberEstimate(memberEstimate)
                .build();

        when(chattingRoomRepository.findById(chattingRoomId)).thenReturn(Optional.of(chattingRoom));

        //when
        chattingRoomService.delete(chattingRoomId);

        //then
        verify(chattingRoomRepository, times(1)).delete(chattingRoom);
    }
}
