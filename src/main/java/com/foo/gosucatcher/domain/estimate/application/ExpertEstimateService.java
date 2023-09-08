package com.foo.gosucatcher.domain.estimate.application;

import static com.foo.gosucatcher.global.error.ErrorCode.ALREADY_REGISTERED_SUB_ITEMS;
import static com.foo.gosucatcher.global.error.ErrorCode.ALREADY_REQUESTED_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT_RESPONSE_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_SUB_ITEM;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertAutoEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertNormalEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertNormalEstimateResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimateRepository;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpertEstimateService {

	private final ExpertEstimateRepository expertEstimateRepository;
	private final MemberRequestEstimateRepository memberRequestEstimateRepository;
	private final ExpertRepository expertRepository;
	private final SubItemRepository subItemRepository;

	public ExpertNormalEstimateResponse createNormal(Long expertId, Long memberEstimateId, ExpertNormalEstimateCreateRequest request) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		MemberRequestEstimate memberRequestEstimate = memberRequestEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		checkAlreadyResponded(memberRequestEstimate);

		ExpertEstimate expertNormalEstimate = ExpertNormalEstimateCreateRequest.toExpertEstimate(
			request, memberRequestEstimate, expert);


		expertEstimateRepository.save(expertNormalEstimate);

		return ExpertNormalEstimateResponse.from(expertNormalEstimate);
	}

	public ExpertAutoEstimateResponse createAuto(Long expertId, ExpertAutoEstimateCreateRequest request) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		SubItem subItem = subItemRepository.findById(request.subItemId())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_SUB_ITEM));

		checkAlreadyRegisteredByExpertWithSubItem(expert, subItem);

		ExpertEstimate expertAutoEstimate = ExpertAutoEstimateCreateRequest.toExpertEstimate(request, expert, subItem);

		expertEstimateRepository.save(expertAutoEstimate);

		return ExpertAutoEstimateResponse.from(expertAutoEstimate);
	}

	@Transactional(readOnly = true)
	public ExpertEstimatesResponse findAll() {
		List<ExpertEstimate> expertEstimateList = expertEstimateRepository.findAllWithFetchJoin();
		return ExpertEstimatesResponse.from(expertEstimateList);
	}

	@Transactional(readOnly = true)
	public ExpertEstimateResponse findById(Long id) {
		ExpertEstimate expertEstimate = expertEstimateRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		return ExpertEstimateResponse.from(expertEstimate);
	}

	public void delete(Long id) {
		ExpertEstimate expertEstimate = expertEstimateRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		expertEstimateRepository.delete(expertEstimate);
	}

	private void checkAlreadyResponded(MemberRequestEstimate memberRequestEstimate) {
		List<ExpertEstimate> expertEstimateList = memberRequestEstimate.getExpertEstimateList();
		expertEstimateList.stream()
			.filter(expertEstimate -> {
				Long registeredEstimateId = expertEstimate.getMemberRequestEstimate().getId();
				Long requestedEstimateId = memberRequestEstimate.getId();
				return registeredEstimateId.equals(requestedEstimateId);
			})
			.forEach(expertEstimate -> {
				throw new BusinessException(ALREADY_REQUESTED_ESTIMATE);
			});
	}

	private void checkAlreadyRegisteredByExpertWithSubItem(Expert expert, SubItem subItem) {
		if (expertEstimateRepository.existsByExpertAndSubItemAndMemberRequestEstimateIsNull(expert, subItem)) {
			throw new BusinessException(ALREADY_REGISTERED_SUB_ITEMS);
		}
	}
}
