package com.foo.gosucatcher.domain.member.application;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberLogInResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberProfileRepository memberProfileRepository;

	public void signUp(@Validated MemberSignUpRequest memberSignUpRequest) {
		Member member = MemberSignUpRequest.toMember(memberSignUpRequest);

		String email = member.getEmail();
		checkDuplicatedEmail(email);

		Member savedMember = memberRepository.save(member);
		memberProfileRepository.initializeMemberProfile(savedMember);
	}

	@Transactional(readOnly = true)
	public MemberLogInResponse logIn(@Validated MemberLogInRequest memberLogInRequest) {
		String email = memberLogInRequest.email();
		Member logInMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		String password = memberLogInRequest.password();
		boolean isSuccess = logInMember.logIn(password);
		if (!isSuccess) {
			throw new InvalidValueException(ErrorCode.LOG_IN_FAILURE);
		}

		return MemberLogInResponse.from(logInMember);
	}

	@Transactional(readOnly = true)
	public MemberPasswordFoundResponse findPassword(@Validated @Email String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		//todo: 추후 유저 이메일로 임시 비밀번호 발급하는 걸로 바꿀 수 있을 듯
		return MemberPasswordFoundResponse.from(member);
	}

	@Transactional(readOnly = true)
	public void checkDuplicatedEmail(@Validated @Email String email) {
		memberRepository.findByEmail(email)
			.ifPresent((elem) -> {
				throw new InvalidValueException(ErrorCode.DUPLICATED_MEMBER_EMAIL);
			});
	}

	public long changeMemberInfo(@Validated @Min(0) long memberId,
		@Validated MemberInfoChangeRequest memberInfoChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Member changedMember = MemberInfoChangeRequest.toMember(memberInfoChangeRequest);
		member.changeMemberInfo(changedMember);

		return memberId;
	}

	public void deleteMember(@Validated @Min(0) long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		member.deleteMember();
	}

	public void uploadProfileImage(@Validated ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.changeProfileImageFile(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(@Validated @Min(0) long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void removeProfileImage(@Validated @Min(0) long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		ImageFile profileImageFile = member.getProfileImageFile();
		memberProfileRepository.deleteImage(profileImageFile);
	}
}
