package com.foo.gosucatcher.domain.estimate.application;

import static com.foo.gosucatcher.global.error.ErrorCode.*;
import static com.foo.gosucatcher.global.error.ErrorCode.DUPLICATE_MEMBER_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_EXPERT_ESTIMATE;
import static com.foo.gosucatcher.global.error.ErrorCode.NOT_FOUND_MEMBER_ESTIMATE;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimatesResponse;
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
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.exception.BusinessException;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberEstimateService {

	private final MemberEstimateRepository memberEstimateRepository;
	private final MemberRepository memberRepository;
	private final SubItemRepository subItemRepository;
	private final ExpertEstimateRepository expertEstimateRepository;
	private final ExpertRepository expertRepository;
	private final ExpertItemRepository expertItemRepository;

	public MemberEstimate create(Long memberId, MemberEstimateRequest memberEstimateRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));
		SubItem subItem = subItemRepository.findById(memberEstimateRequest.subItemId())
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_SUB_ITEM));

		checkDuplicatedMemberEstimate(member.getId(), subItem.getId());

		MemberEstimate memberEstimate = MemberEstimateRequest.toMemberEstimate(member, subItem, memberEstimateRequest);

		return memberEstimateRepository.save(memberEstimate);
	}

	public MemberEstimateResponse createNormal(Long memberId, Long expertId, MemberEstimateRequest memberEstimateRequest) {
		checkExpertHasSubItem(expertId, memberEstimateRequest.subItemId());

		MemberEstimate memberEstimate = create(memberId, memberEstimateRequest);

		Expert expert = expertRepository.findById(expertId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT));

		memberEstimate.updateExpert(expert);

		return MemberEstimateResponse.from(memberEstimate);
	}

	public MemberEstimateResponse createAuto(Long memberId, MemberEstimateRequest memberEstimateRequest) {
		MemberEstimate memberEstimate = create(memberId, memberEstimateRequest);

		return MemberEstimateResponse.from(memberEstimate);
	}

	@Transactional(readOnly = true)
	public MemberEstimatesResponse findAll() {
		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAll();

		return MemberEstimatesResponse.from(memberEstimates);
	}

	@Transactional(readOnly = true)
	public MemberEstimatesResponse findAllByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER));

		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByMember(member);

		return MemberEstimatesResponse.from(memberEstimates);
	}

	@Transactional(readOnly = true)
	public MemberEstimateResponse findById(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		return MemberEstimateResponse.from(memberEstimate);
	}

	@Transactional(readOnly = true)
	public MemberEstimatesResponse findAllPendingNormalByExpertId(Long expertId) {
		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByPendingAndExpertId(expertId);

		return MemberEstimatesResponse.from(memberEstimates);
	}

	public void delete(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		memberEstimateRepository.delete(memberEstimate);
	}

	public Long updateExpertEstimates(Long memberEstimateId, List<ExpertAutoEstimateResponse> expertAutoEstimateResponses) {
		expertAutoEstimateResponses.stream()
			.map(ExpertAutoEstimateResponse::id)
			.forEach(expertEstimateId -> addExpertEstimateToMemberEstimate(memberEstimateId, expertEstimateId));

		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		Status nextStatus = Status.findNextStatus(memberEstimate.getStatus());
		memberEstimate.updateStatus(nextStatus);

		return memberEstimateId;
	}

	private void addExpertEstimateToMemberEstimate(Long memberEstimateId, Long expertEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_MEMBER_ESTIMATE));

		ExpertEstimate expertEstimate = expertEstimateRepository.findById(expertEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(NOT_FOUND_EXPERT_ESTIMATE));

		memberEstimate.addExpertEstimate(expertEstimate);
	}

	private void checkDuplicatedMemberEstimate(Long memberId, Long subItemId) {
		List<MemberEstimate> memberEstimatesForDuplicate = memberEstimateRepository.findByMemberIdAndSubItemIdAndIsNotClosed(
			memberId, subItemId);

		Optional.ofNullable(memberEstimatesForDuplicate).filter(result -> !result.isEmpty()).ifPresent(result -> {
			throw new BusinessException(DUPLICATE_MEMBER_ESTIMATE);
		});
	}

	private void checkExpertHasSubItem(Long expertId, Long subItemId) {
		if (!expertItemRepository.existsByExpertIdAndSubItemId(expertId, subItemId)) {
			throw new BusinessException(NOT_FOUND_EXPERT_ITEM);
		}
	}
}
