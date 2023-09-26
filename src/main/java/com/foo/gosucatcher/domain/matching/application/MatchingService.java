package com.foo.gosucatcher.domain.matching.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.chat.application.ChattingRoomService;
import com.foo.gosucatcher.domain.chat.application.MessageService;
import com.foo.gosucatcher.domain.chat.application.dto.response.ChattingRoomsResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessageResponse;
import com.foo.gosucatcher.domain.chat.application.dto.response.MessagesResponse;
import com.foo.gosucatcher.domain.estimate.application.ExpertEstimateService;
import com.foo.gosucatcher.domain.estimate.application.MemberEstimateService;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertNormalEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class MatchingService {

	private final MemberEstimateService memberEstimateService;
	private final ExpertEstimateService expertEstimateService;
	private final ChattingRoomService chattingRoomService;
	private final MessageService messageService;

	public MessagesResponse match(MemberEstimateResponse memberEstimateResponse) {
		ExpertAutoEstimatesResponse expertAutoEstimatesResponse = expertEstimateService.findAllByConditions(memberEstimateResponse.subItemResponse().id(), memberEstimateResponse.location());

		Long memberEstimateId = memberEstimateService.updateExpertEstimates(memberEstimateResponse.id(), expertAutoEstimatesResponse.expertAutoEstimateResponses());

		return sendFirstMessageForAuto(memberEstimateId, expertAutoEstimatesResponse);
	}

	public MessageResponse sendFirstMessageForNormal(Long memberEstimateId, ExpertNormalEstimateResponse expertNormalEstimateResponse) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.create(memberEstimateId);

		return messageService.sendExpertEstimateMessageForNormal(chattingRoomsResponse.chattingRoomsResponse().get(0), expertNormalEstimateResponse);
	}

	private MessagesResponse sendFirstMessageForAuto(Long memberEstimateId, ExpertAutoEstimatesResponse expertAutoEstimatesResponse) {
		ChattingRoomsResponse chattingRoomsResponse = chattingRoomService.create(memberEstimateId);

		return messageService.sendExpertEstimateMessageForAuto(chattingRoomsResponse.chattingRoomsResponse(),expertAutoEstimatesResponse.expertAutoEstimateResponses());
	}
}
