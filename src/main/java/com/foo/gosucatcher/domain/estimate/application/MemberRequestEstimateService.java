package com.foo.gosucatcher.domain.estimate.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimate;
import com.foo.gosucatcher.domain.estimate.domain.MemberRequestEstimateRepository;
import com.foo.gosucatcher.domain.estimate.application.dto.request.MemberRequestEstimateRequest;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimateResponse;
import com.foo.gosucatcher.domain.estimate.application.dto.response.MemberRequestEstimatesResponse;
import com.foo.gosucatcher.domain.item.domain.SubItem;
import com.foo.gosucatcher.domain.item.domain.SubItemRepository;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberRequestEstimateService {

	private final MemberRequestEstimateRepository memberRequestEstimateRepository;
	private final MemberRepository memberRepository;
	private final SubItemRepository subItemRepository;

	public MemberRequestEstimateResponse create(Long memberId,
		MemberRequestEstimateRequest memberRequestEstimateRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		SubItem subItem = subItemRepository.findById(memberRequestEstimateRequest.subItemId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_SUB_ITEM));

		MemberRequestEstimate memberRequestEstimate = MemberRequestEstimateRequest.toMemberRequestEstimate(member,
			subItem, memberRequestEstimateRequest);

		MemberRequestEstimate savedMemberRequestEstimate = memberRequestEstimateRepository.save(memberRequestEstimate);

		return MemberRequestEstimateResponse.from(savedMemberRequestEstimate);
	}

	@Transactional(readOnly = true)
	public MemberRequestEstimatesResponse findAll() {
		List<MemberRequestEstimate> memberRequestEstimates = memberRequestEstimateRepository.findAll();

		return MemberRequestEstimatesResponse.from(memberRequestEstimates);
	}

	@Transactional(readOnly = true)
	public MemberRequestEstimatesResponse findAllByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		List<MemberRequestEstimate> memberRequestEstimates = memberRequestEstimateRepository.findAllByMember(member);

		return MemberRequestEstimatesResponse.from(memberRequestEstimates);
	}

	@Transactional(readOnly = true)
	public MemberRequestEstimateResponse findById(Long memberRequestEstimateId) {
		MemberRequestEstimate memberRequestEstimate = memberRequestEstimateRepository.findById(memberRequestEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		return MemberRequestEstimateResponse.from(memberRequestEstimate);
	}

	public Long update(Long memberRequestEstimateId, MemberRequestEstimateRequest memberRequestEstimateRequest) {
		MemberRequestEstimate foundMemberRequestEstimate = memberRequestEstimateRepository.findById(
				memberRequestEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		MemberRequestEstimate memberRequestEstimate = MemberRequestEstimateRequest.toMemberRequestEstimate(
			foundMemberRequestEstimate.getMember(), foundMemberRequestEstimate.getSubItem(),
			memberRequestEstimateRequest);

		foundMemberRequestEstimate.update(memberRequestEstimate);

		return foundMemberRequestEstimate.getId();
	}

	public void delete(Long memberRequestEstimateId) {
		MemberRequestEstimate memberRequestEstimate = memberRequestEstimateRepository.findById(memberRequestEstimateId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_REQUEST_ESTIMATE));

		memberRequestEstimateRepository.delete(memberRequestEstimate);
	}
}
