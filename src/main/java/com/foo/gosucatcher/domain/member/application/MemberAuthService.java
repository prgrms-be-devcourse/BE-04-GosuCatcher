package com.foo.gosucatcher.domain.member.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.expert.domain.ExpertRepository;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberLoginRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberPasswordFoundRequest;
import com.foo.gosucatcher.domain.member.application.dto.request.MemberSignupRequest;
import com.foo.gosucatcher.domain.member.application.dto.response.JwtReissueResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberCertifiedResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberPasswordFoundResponse;
import com.foo.gosucatcher.domain.member.application.dto.response.MemberSignupResponse;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberImage;
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.domain.Roles;
import com.foo.gosucatcher.domain.member.exception.EmailAuthException;
import com.foo.gosucatcher.domain.member.exception.MemberCertifiedFailException;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.security.CustomUserDetails;
import com.foo.gosucatcher.global.security.JwtTokenProvider;
import com.foo.gosucatcher.global.util.RandomNumberUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberAuthService {

	private final MemberEmailAuthService memberEmailAuthService;
	private final MemberRepository memberRepository;
	private final ExpertRepository expertRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public MemberSignupResponse signup(MemberSignupRequest memberSignUpRequest) {
		checkDuplicatedEmail(memberSignUpRequest);

		Member signupMember = MemberSignupRequest.toMember(memberSignUpRequest);

		String password = signupMember.getPassword();
		signupMember.changePassword(password, passwordEncoder);
		signupMember.updateMemberRole(Roles.ROLE_USER);
		Member savedMember = memberRepository.save(signupMember);

		Expert expert = Expert.builder()
			.member(savedMember)
			.build();
		expertRepository.save(expert);

		MemberImage defaultMemberImage = new MemberImage(MemberImage.DEFAULT_PATH);
		signupMember.updateProfileImage(defaultMemberImage);

		return MemberSignupResponse.from(savedMember);
	}

	@Transactional
	public MemberCertifiedResponse login(MemberLoginRequest memberLoginRequest) {
		String loginRequestEmail = memberLoginRequest.email();
		Member member = memberRepository.findByEmail(loginRequestEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		Long memberId = member.getId();
		Expert expert = expertRepository.findByMemberId(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		Member loginRequestMember = MemberLoginRequest.toMember(memberLoginRequest);
		member.authenticate(loginRequestMember, passwordEncoder);

		return getMemberCertifiedResponse(member, expert);
	}

	public void logout(String memberEmail) {
		Member member = memberRepository.findByEmail(memberEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		member.logout();
	}

	public void deleteMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));
		Expert expert = expertRepository.findByMemberIdWithFetchJoin(memberId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_EXPERT));

		expertRepository.delete(expert);
		memberRepository.delete(member);
	}

	public MemberPasswordFoundResponse findPassword(MemberPasswordFoundRequest memberPasswordFoundRequest) {
		String requestEmail = memberPasswordFoundRequest.email();
		Member member = memberRepository.findByEmail(requestEmail)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FOUND_MEMBER));

		String requestName = memberPasswordFoundRequest.name();
		member.isSameMemberName(requestName);

		String temporaryPassword = RandomNumberUtils.createTemporaryStringPassword();
		member.changePassword(temporaryPassword, passwordEncoder);

		memberEmailAuthService.sendPasswordRecoveryEmail(temporaryPassword, requestEmail);

		return MemberPasswordFoundResponse.from(member);
	}

	public JwtReissueResponse reissue(String refreshToken) {
		jwtTokenProvider.checkValidRefreshToken(refreshToken);

		refreshToken = jwtTokenProvider.removeBearer(refreshToken);
		CustomUserDetails customUserDetails = (CustomUserDetails)(jwtTokenProvider.getCustomUserDetailsByRefreshToken(
			refreshToken));

		Member member = customUserDetails.getMember();
		Expert expert = customUserDetails.getExpert();

		String memberRefreshToken = member.getRefreshToken();
		if (!memberRefreshToken.equals(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.NOT_VALID_REFRESH_TOKEN);
		}

		return createJwtReissueResponse(member, expert);
	}

	private MemberCertifiedResponse getMemberCertifiedResponse(Member member, Expert expert) {
		String accessToken = jwtTokenProvider.createAccessToken(member, expert);
		String refreshToken = jwtTokenProvider.createRefreshToken(member, expert);

		member.refreshToken(refreshToken);

		log.info("accessToken 생성 완료 : {}", accessToken);
		log.info("refreshToken 생성 완료 : {}", refreshToken);

		return MemberCertifiedResponse.from(accessToken, refreshToken);
	}

	private JwtReissueResponse createJwtReissueResponse(Member member, Expert expert) {
		String reissuedAccessToken = jwtTokenProvider.createAccessToken(member, expert);

		return new JwtReissueResponse(reissuedAccessToken);
	}

	private void checkDuplicatedEmail(MemberSignupRequest memberSignUpRequest) {
		if (memberRepository.existsByEmail(memberSignUpRequest.email())) {
			throw new EmailAuthException(ErrorCode.DUPLICATED_MEMBER);
		}
	}
}
