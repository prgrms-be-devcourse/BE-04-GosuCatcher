package com.foo.gosucatcher.domain.chat.application;

import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.Status;
import com.foo.gosucatcher.domain.expert.domain.Expert;
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

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ExpertEstimateRepository expertEstimateRepository;

    @InjectMocks
    private ChattingRoomService chattingRoomService;

    private Member member;
    private MainItem mainItem;
    private SubItem subItem;
    private MemberEstimate memberEstimate;
    private Expert expert;
    private ExpertEstimate expertEstimate;


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

        expert = Expert.builder()
            .location("서울시 강남구")
            .rating(4.0)
            .maxTravelDistance(10)
            .reviewCount(5)
            .storeName("업체명")
            .description("추가 설명입니다")
            .member(member)
            .build();

        expertEstimate = ExpertEstimate.builder()
            .expert(expert)
            .memberEstimate(memberEstimate)
            .subItem(subItem)
            .totalCost(10000)
            .description("견적서입니다")
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
        assertThat(chattingRoomResponses).hasSize(2);
    }

    @DisplayName("회원 요청서에 해당하는 모든 채팅방 조회 테스트")
    @Test
    void findAllByMemberEstimateId() {
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
        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAllByMemberEstimateId(memberEstimateId);
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(chattingRoomResponses).hasSize(2);
    }

    @DisplayName("회원 별 전체 채팅방 조회 테스트")
    @Test
    void findAllByMemberId() {
        //given
        Long memberId = 1L;

        ChattingRoom chattingRoom = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        ChattingRoom chattingRoom2 = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        when(memberEstimateRepository.findAllByMember(any(Member.class))).thenReturn(List.of(memberEstimate));

        List<ChattingRoom> expectedChattingRooms = List.of(chattingRoom, chattingRoom2);

        when(chattingRoomRepository.findAllByMemberEstimate(memberEstimate)).thenReturn(expectedChattingRooms);

        //when
        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAllByMemberId(memberId);
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(chattingRoomResponses).hasSize(2);
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

    @DisplayName("고수 별 전체 채팅방 조회 테스트")
    @Test
    void findAllByExpertId() {
        //given
        Long expertId = 1L;

        ChattingRoom chattingRoom = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        ChattingRoom chattingRoom2 = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        ExpertEstimate expertEstimate2 = ExpertEstimate.builder()
            .expert(expert)
            .memberEstimate(memberEstimate)
            .subItem(subItem)
            .totalCost(10000)
            .description("견적서입니다2")
            .build();

        when(expertEstimateRepository.findAllByExpertIdAndMemberEstimateIsNotNull(expertId)).thenReturn(List.of(expertEstimate, expertEstimate2));

        when(chattingRoomRepository.findAllByMemberEstimate(memberEstimate)).thenReturn(List.of(chattingRoom, chattingRoom2));

        //when
        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAllByExpertId(expertId);
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(chattingRoomResponses).hasSize(4);
    }

    @DisplayName("고수의 일반 견적 관련 채팅방 목록 조회 테스트")
    @Test
    void findAllOfNormalByExpertId() {
        //given
        Long expertId = 1L;

        ChattingRoom chattingRoom = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        ChattingRoom chattingRoom2 = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        when(memberEstimateRepository.findAllByExpertId(expertId)).thenReturn(List.of(memberEstimate));
        when(chattingRoomRepository.findAllByMemberEstimate(memberEstimate)).thenReturn(List.of(chattingRoom, chattingRoom2));

        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAllOfNormalByExpertId(expertId);
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(chattingRoomResponses).hasSize(2);
    }

    @DisplayName("고수의 바로 견적 관련 채팅방 목록 조회 테스트")
    @Test
    void findAllOfAutoByExpertId() {
        //given
        Long expertId = 1L;

        memberEstimate.updateStatus(Status.PROCEEDING);

        ChattingRoom chattingRoom = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        ChattingRoom chattingRoom2 = ChattingRoom.builder()
            .memberEstimate(memberEstimate)
            .build();

        ExpertEstimate expertEstimate2 = ExpertEstimate.builder()
            .expert(null)
            .memberEstimate(memberEstimate)
            .subItem(subItem)
            .totalCost(10000)
            .description("견적서입니다2")
            .build();

        when(expertEstimateRepository.findAllByExpertIdAndMemberEstimateIsNotNull(expertId)).thenReturn(List.of(expertEstimate, expertEstimate2));
        when(chattingRoomRepository.findAllByMemberEstimate(memberEstimate)).thenReturn(List.of(chattingRoom, chattingRoom2));

        //when
        ChattingRoomsResponse chattingRoomResponse = chattingRoomService.findAllOfAutoByExpertId(expertId);
        List<ChattingRoomResponse> chattingRoomResponses = chattingRoomResponse.chattingRoomsResponse();

        //then
        assertThat(chattingRoomResponses).hasSize(4);

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
