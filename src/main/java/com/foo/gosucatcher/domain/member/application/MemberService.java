package com.foo.gosucatcher.domain.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.application.dto.request.MemberInfoChangeRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLogInRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignUpRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.ProfileImageUploadRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final MemberProfileRepository memberProfileRepository;

	public MemberService(MemberRepository memberRepository, MemberProfileRepository memberProfileRepository) {
		this.memberRepository = memberRepository;
		this.memberProfileRepository = memberProfileRepository;
	}

	public void signUp(MemberSignUpRequest memberSignUpRequest) {
		Member member = MemberSignUpRequest.to(memberSignUpRequest);

		String email = member.getEmail();
		checkDuplicatedEmail(email);

		memberRepository.save(member);
		memberProfileRepository.initializeMemberProfile(member);
	}

	@Transactional(readOnly = true)
	public void logIn(MemberLogInRequest memberLogInRequest) {
		String email = memberLogInRequest.email();
		Member logInMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		String password = memberLogInRequest.password();
		boolean isSuccess = logInMember.logIn(password);
		if (!isSuccess) {
			throw new InvalidValueException(ErrorCode.LOG_IN_FAILURE);
		}
	}

	@Transactional(readOnly = true)
	public MemberPasswordFoundResponse findPassword(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		//todo: 추후에 유저인증된 이메일로 임시비밀번호 발급하는 걸로 바꿀 수 있을 듯요?!
		return MemberPasswordFoundResponse.to(member);
	}

	@Transactional(readOnly = true)
	public void checkDuplicatedEmail(String email) {
		memberRepository.findByEmail(email)
			.ifPresent((elem) -> {
				throw new InvalidValueException(ErrorCode.DUPLICATED_MEMBER_EMAIL);
			});
	}

	public void changeMemberInfo(long memberId, MemberInfoChangeRequest memberInfoChangeRequest) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		member.changeMemberInfo(memberInfoChangeRequest);
	}

	public void deleteMember(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		member.deleteMember();
	}

	public void uploadProfileImage(ProfileImageUploadRequest profileImageUploadRequest) {
		long memberId = profileImageUploadRequest.memberId();
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		MultipartFile file = profileImageUploadRequest.file();
		ImageFile imageFile = memberProfileRepository.uploadImage(member, file);

		member.changeProfileImageFile(imageFile);
	}

	@Transactional(readOnly = true)
	public ImageFile findProfileImage(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		return memberProfileRepository.findImage(member);
	}

	public void removeProfileImage(long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		memberProfileRepository.deleteImage(member);
	}
}
