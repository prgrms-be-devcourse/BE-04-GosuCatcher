package com.foo.gosucatcher.domain.estimate.application;

import java.util.List;
import java.util.Optional;

import com.foo.gosucatcher.domain.estimate.application.dto.response.ExpertAutoEstimateResponse;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimate;
import com.foo.gosucatcher.domain.estimate.domain.ExpertEstimateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberEstimatesResponse;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberEstimateRepository;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
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

	public MemberEstimateResponse create(Long memberId, MemberEstimateRequest memberEstimateRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		SubItem subItem = subItemRepository.findById(memberEstimateRequest.subItemId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		checkDuplicatedMemberEstimate(member.getId(), subItem.getId());

		MemberEstimate memberEstimate = MemberEstimateRequest.toMemberEstimate(member, subItem, memberEstimateRequest);

		MemberEstimate savedMemberEstimate = memberEstimateRepository.save(memberEstimate);

		return MemberEstimateResponse.from(savedMemberEstimate);
	}

	@Transactional(readOnly = true)
	public MemberEstimatesResponse findAll() {
		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAll();

		return MemberEstimatesResponse.from(memberEstimates);
	}

	@Transactional(readOnly = true)
	public MemberEstimatesResponse findAllByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		List<MemberEstimate> memberEstimates = memberEstimateRepository.findAllByMember(member);

		return MemberEstimatesResponse.from(memberEstimates);
	}

	@Transactional(readOnly = true)
	public MemberEstimateResponse findById(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

		return MemberEstimateResponse.from(memberEstimate);
	}

	public void delete(Long memberEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

		memberEstimateRepository.delete(memberEstimate);
	}

	public Long updateExpertEstimates(Long memberEstimateId, List<ExpertAutoEstimateResponse> expertAutoEstimateResponses) {
		expertAutoEstimateResponses.stream()
				.map(ExpertAutoEstimateResponse::id)
				.forEach(expertEstimateId -> addExpertEstimateToMemberEstimate(memberEstimateId, expertEstimateId));

		return memberEstimateId;
	}

	public void addExpertEstimateToMemberEstimate(Long memberEstimateId, Long expertEstimateId) {
		MemberEstimate memberEstimate = memberEstimateRepository.findById(memberEstimateId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_ESTIMATE));

		ExpertEstimate expertEstimate = expertEstimateRepository.findById(expertEstimateId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT_RESPONSE_ESTIMATE));

		memberEstimate.addExpertEstimate(expertEstimate);
	}

	private void checkDuplicatedMemberEstimate(Long memberId, Long subItemId) {
		List<MemberEstimate> memberEstimatesForDuplicate = memberEstimateRepository.findByMemberIdAndSubItemIdAndIsNotClosed(
			memberId, subItemId);

		Optional.ofNullable(memberEstimatesForDuplicate).filter(result -> !result.isEmpty()).ifPresent(result -> {
			throw new BusinessException(ErrorCode.DUPLICATE_MEMBER_ESTIMATE);
		});
	}
}
