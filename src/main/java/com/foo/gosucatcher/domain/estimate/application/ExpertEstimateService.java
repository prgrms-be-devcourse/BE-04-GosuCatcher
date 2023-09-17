package com.foo.gosucatcher.domain.estimate.application;

import static com.foo.gosucatcher.global.error.ErrorCode.ALREADY_REGISTERED_SUB_ITEMS;
import static com.foo.gosucatcher.global.error.ErrorCode.ALREADY_REQUESTED_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_SUB_ITEM;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_REGISTERED_SUB_ITEMS;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertAutoEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.request.ExpertNormalEstimateCreateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertNormalEstimateResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.estimate.domain.Status;
import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertItemRepository;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.util.RandomElementSelector;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ExpertEstimateService {

	private final ExpertEstimateRepository expertEstimateRepository;
	private final MemberEstimateRepository memberEstimateRepository;
	private final ExpertRepository expertRepository;
	private final SubItemRepository subItemRepository;
	private final ExpertItemRepository expertItemRepository;

	public ExpertNormalEstimateResponse createNormal(Long expertId, Long memberEstimateId, ExpertNormalEstimateCreateRequest request) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		checkAlreadyResponded(memberEstimate);

		ExpertEstimate expertNormalEstimate = ExpertNormalEstimateCreateRequest.toExpertEstimate(
			request, memberEstimate, expert);

		Status nextStatus = Status.findNextStatus(memberEstimate.getStatus());
		memberEstimate.updateStatus(nextStatus);
		memberEstimate.updateExpert(expert);

		expertEstimateRepository.save(expertNormalEstimate);

		return ExpertNormalEstimateResponse.from(expertNormalEstimate);
	}

	public ExpertAutoEstimateResponse createAuto(Long expertId, ExpertAutoEstimateCreateRequest request) {
		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		SubItem subItem = subItemRepository.findById(request.subItemId())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_SUB_ITEM));

		checkExpertHasSubItem(expert.getId(), subItem.getId());
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
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT_ESTIMATE));

		return ExpertEstimateResponse.from(expertEstimate);
	}

	public void delete(Long id) {
		ExpertEstimate expertEstimate = expertEstimateRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT_ESTIMATE));

		expertEstimateRepository.delete(expertEstimate);
	}

	@Transactional(readOnly = true)
	public ExpertAutoEstimatesResponse findAllByConditions(Long subItemId, String activityLocation) {
		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllBySubItemIdAndLocation(
			subItemId, activityLocation);

		List<ExpertEstimate> randomExpertEstimates = RandomElementSelector.selectRandomElements(expertEstimates, 10);

		return ExpertAutoEstimatesResponse.from(randomExpertEstimates);
	}

	@Transactional(readOnly = true)
	public ExpertEstimatesResponse findAllByMemberEstimateId(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllByMemberEstimate(memberEstimate);

		return ExpertEstimatesResponse.from(expertEstimates);
	}

	@Transactional(readOnly = true)
	public ExpertAutoEstimatesResponse findAllUnmatchedAutoByExpertId(Long expertId) {
		List<ExpertEstimate> expertEstimates = expertEstimateRepository.findAllByExpertIdAndMemberEstimateIsNull(expertId);

		return ExpertAutoEstimatesResponse.from(expertEstimates);
	}

	private void checkAlreadyResponded(MemberEstimate memberEstimate) {
		List<ExpertEstimate> expertEstimateList = memberEstimate.getExpertEstimateList();
		expertEstimateList.stream()
			.filter(expertEstimate -> {
				Long registeredEstimateId = expertEstimate.getMemberEstimate().getId();
				Long requestedEstimateId = memberEstimate.getId();
				return registeredEstimateId.equals(requestedEstimateId);
			})
			.forEach(expertEstimate -> {
				throw new BusinessException(ALREADY_REQUESTED_ESTIMATE);
			});
	}

	private void checkAlreadyRegisteredByExpertWithSubItem(Expert expert, SubItem subItem) {
		if (expertEstimateRepository.existsByExpertAndSubItemAndMemberEstimateIsNull(expert, subItem)) {

			throw new BusinessException(ALREADY_REGISTERED_SUB_ITEMS);
		}
	}

	private void checkExpertHasSubItem(Long expertId, Long subItemId) {
		if (!expertItemRepository.existsByExpertIdAndSubItemId(expertId, subItemId)) {
			throw new BusinessException(NOT_REGISTERED_SUB_ITEMS);
		}
	}
}
