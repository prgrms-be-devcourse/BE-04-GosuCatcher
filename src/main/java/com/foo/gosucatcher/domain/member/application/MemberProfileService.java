package com.foo.gosucatcher.domain.member.application;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foo.gosucatcher.domain.image.ImageService;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageDeleteRequest;
import com.foo.gosucatcher.domain.image.application.dto.request.ImageUploadRequest;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageResponse;
import com.foo.gosucatcher.domain.image.application.dto.response.ImageUploadResponse;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberProfileChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileChangeResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberProfileResponse;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberImage;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberProfileService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;

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

	public ImageUploadResponse uploadProfileImage(Long memberId, ImageUploadRequest request) {
		ImageUploadResponse uploadResponse = imageService.store(request);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		List<String> filenames = uploadResponse.filenames();
		String filename = filenames.get(0);
		MemberImage newProfileImage = new MemberImage(filename);

		member.updateProfileImage(newProfileImage);

		memberRepository.save(member);

		return uploadResponse;
	}

	@Transactional(readOnly = true)
	public ImageResponse getProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		String filename = member.getProfileMemberImage().getFilename();

		return new ImageResponse(List.of(filename));
	}

	public void deleteProfileImage(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String filename = member.getProfileMemberImage().getFilename();
		imageService.delete(new ImageDeleteRequest(List.of(filename)));

		member.getProfileMemberImage().changePathToDefault();
		memberRepository.save(member);
	}
}
