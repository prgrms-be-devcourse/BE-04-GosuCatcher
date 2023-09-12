package com.foo.gosucatcher.domain.member.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberProfileService {

	private final MemberProfileRepository memberProfileRepository;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		memberRepository.delete(member);
	}

	public MemberProfileResponse findMemberProfile(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return MemberProfileResponse.from(member);
	}

	public MemberProfileChangeResponse changeMemberProfile(Long memberId,
		@Validated MemberProfileChangeRequest memberProfileChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member changedMember = MemberProfileChangeRequest.toMember(memberProfileChangeRequest);
		member.updateProfile(changedMember, passwordEncoder);

		return MemberProfileChangeResponse.from(member);
	}

	public void uploadProfileImage(ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.updateProfileImage(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void deleteProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		ImageFile profileImageFile = member.getProfileImageFile();
		memberProfileRepository.deleteImage(profileImageFile);
	}
}
