package com.foo.gosucatcher.domain.member.application;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignUpResponse;
import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.domain.Roles;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.error.exception.InvalidValueException;
import com.foo.gosucatcher.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Transactional
@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberProfileRepository memberProfileRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final BCryptPasswordEncoder passwordEncoder;

	public MemberSignUpResponse signUp(@Validated MemberSignUpRequest memberSignUpRequest) {
		String signUpEmail = memberSignUpRequest.email();
		String signUpPassword = memberSignUpRequest.password();
		String signUpName = memberSignUpRequest.name();

		checkDuplicatedEmail(signUpEmail);

		String encodedPassword = passwordEncoder.encode(signUpPassword);
		Member signUpMember = Member.builder()
			.email(signUpEmail)
			.password(encodedPassword)
			.name(signUpName)
			.role(Roles.USER)
			.build();

		Member savedMember = memberRepository.save(signUpMember);
		memberProfileRepository.initializeMemberProfile(savedMember);

		return MemberSignUpResponse.from(savedMember);
	}

	@Transactional
	public MemberLogInResponse logIn(@Validated MemberLogInRequest memberLogInRequest) {
		String logInEmail = memberLogInRequest.email();
		Member member = memberRepository.findByEmail(logInEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER_EMAIL));

		String logInPassword = memberLogInRequest.password();
		String encodedPassword = member.getPassword();
		boolean matches = passwordEncoder.matches(logInPassword, encodedPassword);
		if (!matches) {
			throw new InvalidValueException(ErrorCode.LOG_IN_FAILURE);
		}

		return getMemberLogInResponse(member);
	}

	public void logOut(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		member.logOut();
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

	private MemberLogInResponse getMemberLogInResponse(Member member) {
		String memberEmail = member.getEmail();
		String accessToken = jwtTokenProvider.createAccessToken(memberEmail);
		String refreshToken = jwtTokenProvider.createRefreshToken(memberEmail);

		member.refresh(refreshToken);

		return MemberLogInResponse.from(accessToken, refreshToken);
	}
}
