package com.foo.gosucatcher.domain.estimate.presentation;

import com.foo.gosucatcher.domain.chat.application.ChattingMessageService;
import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingMessageResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingMessagesResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.foo.gosucatcher.domain.estimate.application.MatchingService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matching")
@RequiredArgsConstructor
public class MatchingController {

	private final MatchingService matchingService;
	private final MemberEstimateService memberEstimateService;
	private final ChattingRoomService chattingRoomService;
	private final ChattingMessageService chattingMessageService;

	@PostMapping("/auto/{memberId}")
	public ResponseEntity<ChattingMessagesResponse> createAutoEstimate(@PathVariable Long memberId,
																	@Validated @RequestBody MemberEstimateRequest memberEstimateRequest) {
		MemberEstimateResponse memberEstimateResponse = memberEstimateService.create(memberId, memberEstimateRequest);

		//매칭된 바로 견적 리스트
		ExpertAutoEstimatesResponse expertAutoEstimatesResponse = matchingService.match(memberEstimateResponse.subItemId(), memberEstimateResponse.location());

		//요청 견적서에 매칭된 바로 견적들 삽입 (update)
		Long memberEstimateId = memberEstimateService.updateExpertEstimates(memberEstimateResponse.id(), expertAutoEstimatesResponse.expertAutoEstimateResponses());

		//채팅방 생성
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.create(memberEstimateId);

		//생성된 채팅방에 메시지 보내기
		ChattingMessagesResponse chattingMessagesResponse = SendExpertEstimateMessage(chattingRoomsResponse.chattingRoomsResponse(), expertAutoEstimatesResponse.expertAutoEstimateResponses());

		return ResponseEntity.ok(chattingMessagesResponse);
	}

    private ChattingMessagesResponse SendExpertEstimateMessage(List<ChattingRoomResponse> chattingRoomResponses, List<ExpertAutoEstimateResponse> expertAutoEstimateResponses) {
        if (chattingRoomResponses.size() != expertAutoEstimateResponses.size()) {
            throw new BusinessException(ErrorCode.CHATTING_ROOM_ASSIGNMENT_FAILED);
        }

        List<ChattingMessageResponse> chattingMessageResponses = new ArrayList<>();

        int chattingRoomCount = chattingRoomResponses.size();

        for (int count = 0; count < chattingRoomCount; count++) {
            ChattingMessageResponse chattingMessageResponse = chattingMessageService.create(expertAutoEstimateResponses.get(count).expert().id(), chattingRoomResponses.get(count).id(), expertAutoEstimateResponses.get(count).description());
            chattingMessageResponses.add(chattingMessageResponse);
        }

        return ChattingMessagesResponse.valueOf(chattingMessageResponses);
    }
}
