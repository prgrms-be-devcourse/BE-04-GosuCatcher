package com.foo.gosucatcher.domain.matching.application;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.MessageService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessagesResponse;
import com.foo.gosucatcher.domain.estimate.application.ExpertEstimateService;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;

@RequiredArgsConstructor
@Service
@Transactional
public class MatchingService {

	private final MemberEstimateService memberEstimateService;
	private final ExpertEstimateService expertEstimateService;
	private final ChattingRoomService chattingRoomService;
	private final MessageService messageService;

	public MessagesResponse match(MemberEstimateResponse memberEstimateResponse) {
		ExpertAutoEstimatesResponse expertAutoEstimatesResponse = expertEstimateService.findAllByConditions(memberEstimateResponse.subItemId(), memberEstimateResponse.location());

		Long memberEstimateId = memberEstimateService.updateExpertEstimates(memberEstimateResponse.id(), expertAutoEstimatesResponse.expertAutoEstimateResponses());

		return sendFirstMessage(memberEstimateId, expertAutoEstimatesResponse);
	}

	private MessagesResponse sendFirstMessage(Long memberEstimateId, ExpertAutoEstimatesResponse expertAutoEstimatesResponse) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.create(memberEstimateId);

		return messageService.sendExpertEstimateMessage(chattingRoomsResponse.chattingRoomsResponse(), expertAutoEstimatesResponse.expertAutoEstimateResponses());
	}
}
