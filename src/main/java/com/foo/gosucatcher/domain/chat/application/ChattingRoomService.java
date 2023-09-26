package com.foo.gosucatcher.domain.chat.application;

import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_CHATTING_ROOM;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER_ESTIMATE;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.chat.application.dto.request.ChattingRoomRequest;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoom;
import com.foo.gosucatcher.domain.chat.domain.ChattingRoomRepository;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.Status;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ChattingRoomService {

	private final MemberEstimateRepository memberEstimateRepository;
	private final ChattingRoomRepository chattingRoomRepository;
	private final MemberRepository memberRepository;
	private final ExpertEstimateRepository expertEstimateRepository;

	public ChattingRoomsResponse create(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		List<ChattingRoom> chattingRooms = memberEstimate.getExpertEstimateList()
			.stream()
			.map(x -> ChattingRoomRequest.toChattingRoom(memberEstimate))
			.toList();

		List<ChattingRoom> savedChattingRooms = chattingRoomRepository.saveAll(chattingRooms);

		chattingRooms.forEach(memberEstimate::addChattingRoom);

		return ChattingRoomsResponse.from(savedChattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAll() {
		List<ChattingRoom> chattingRooms = chattingRoomRepository.findAll();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllByMemberEstimateId(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		List<ChattingRoom> chattingRooms = chattingRoomRepository.findAllByMemberEstimate(memberEstimate);

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByMember(member);

		List<ChattingRoom> chattingRooms = memberEstimates.stream()
			.flatMap(x -> chattingRoomRepository.findAllByMemberEstimate(x).stream())
			.toList();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomResponse findById(Long chattingRoomId) {
		ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

		return ChattingRoomResponse.from(chattingRoom);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllByExpertId(Long expertId) {
		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllByExpertIdAndMemberEstimateIsNotNull(expertId);

		List<MemberEstimate> memberEstimates = expertEstimates.stream()
			.map(ExpertEstimate::getMemberEstimate)
			.toList();

		List<ChattingRoom> chattingRooms = memberEstimates.stream()
			.map(chattingRoomRepository::findAllByMemberEstimate)
			.flatMap(List::stream)
			.toList();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllOfNormalByExpertId(Long expertId) {
		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByExpertId(expertId);

		List<ChattingRoom> chattingRooms = memberEstimates.stream()
			.map(chattingRoomRepository::findAllByMemberEstimate)
			.flatMap(List::stream)
			.toList();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	@Transactional(readOnly = true)
	public ChattingRoomsResponse findAllOfAutoByExpertId(Long expertId) {
		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllByExpertIdAndMemberEstimateIsNotNull(expertId);

		List<MemberEstimate> memberEstimates = expertEstimates.stream()
			.map(ExpertEstimate::getMemberEstimate)
			.filter(memberEstimate -> memberEstimate.getExpert() == null)
			.filter(memberEstimate -> memberEstimate.getStatus() != Status.PENDING)
			.toList();

		List<ChattingRoom> chattingRooms = memberEstimates.stream()
			.map(chattingRoomRepository::findAllByMemberEstimate)
			.flatMap(List::stream)
			.toList();

		return ChattingRoomsResponse.from(chattingRooms);
	}

	public void delete(Long chattingRoomId) {
		ChattingRoom chattingRoom = chattingRoomRepository.findById(chattingRoomId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_CHATTING_ROOM));

		MemberEstimate memberEstimate = chattingRoom.getMemberEstimate();

		chattingRoomRepository.delete(chattingRoom);

		memberEstimate.removeChattingRoom(chattingRoom);
	}
}
