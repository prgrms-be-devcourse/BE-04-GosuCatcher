package com.foo.gosucatcher.domain.chat.application;

import com.foo.gosucatcher.domain.chat.application.dto.request.ChattingRoomRequest;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class ChattingRoomService {

    private final MemberEstimateRepository memberEstimateRepository;
    private final ChattingRoomRepository chattingRoomRepository;

    /***
     * 채팅방 1개 이상 생성 (memberEstimate와 연관되어 있는 모든 ExpertEstimate에 대한 채팅방 생성, 최대 10개)
     * @param memberEstimateId
     * @return ChattingRoomsResponse
     */
    public ChattingRoomsResponse create(Long memberEstimateId) {
        MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

        //고객 요청서에 매칭된 바로 견적서 개수와 동일한 개수의 채팅방 생성
        List<ChattingRoom> chattingRooms = memberEstimate.getExpertEstimateList()
                .stream()
                .map(x -> ChattingRoomRequest.toChattingRoom(memberEstimate))
                .toList();

        chattingRoomRepository.saveAll(chattingRooms);

        return ChattingRoomsResponse.from(chattingRooms);
    }

    @Transactional(readOnly = true)
    public ChattingRoomsResponse findAll() {
        List<ChattingRoom> chattingRooms = chattingRoomRepository.findAll();

        return ChattingRoomsResponse.from(chattingRooms);
    }

    @Transactional(readOnly = true)
    public ChattingRoomsResponse findAllByMemberEstimate(Long memberEstimateId) {
        MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

        List<ChattingRoom> chattingRooms = chattingRoomRepository.findAllByMemberEstimate(memberEstimate);

        return ChattingRoomsResponse.from(chattingRooms);
    }

    @Transactional(readOnly = true)
    public ChattingRoomResponse findById(Long chattingRoomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_CHATTING_ROOM));

        return ChattingRoomResponse.from(chattingRoom);
    }

    public void delete(Long chattingRoomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_CHATTING_ROOM));

        chattingRoomRepository.delete(chattingRoom);
    }
}
