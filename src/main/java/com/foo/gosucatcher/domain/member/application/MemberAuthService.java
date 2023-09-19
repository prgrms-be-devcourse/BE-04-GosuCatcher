package com.foo.gosucatcher.domain.member.application;

import org.springframework.security.core.userdetails.UserDetails;
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
import com.foo.gosucatcher.domain.member.domain.MemberRepository;
import com.foo.gosucatcher.domain.member.domain.Roles;
import com.foo.gosucatcher.domain.member.exception.MemberCertifiedFailException;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.exception.EntityNotFoundException;
import com.foo.gosucatcher.global.security.CustomUserDetails;
import com.foo.gosucatcher.global.security.JwtTokenProvider;
import com.foo.gosucatcher.global.util.RandomNumberUtils;

import lombok.RequiredArgsConstructor;

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
		Member signupMember = MemberSignupRequest.toMember(memberSignUpRequest);

		String password = signupMember.getPassword();
		signupMember.changePassword(password, passwordEncoder);
		signupMember.updateMemberRole(Roles.ROLE_USER);
		Member savedMember = memberRepository.save(signupMember);

		Expert expert = Expert.builder()
			.member(savedMember)
			.build();
		expertRepository.save(expert);

		//todo: 여기에 프로필 사진 초기화 메서드 추가

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
		if (!jwtTokenProvider.isValidRefreshToken(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.NOT_VALID_REFRESH_TOKEN);
		}

		refreshToken = jwtTokenProvider.removeBearer(refreshToken);
		UserDetails userDetails = getCustomUserDetailsByRefreshToken(refreshToken);

		Member member = ((CustomUserDetails)userDetails).getMember();
		Expert expert = ((CustomUserDetails)userDetails).getExpert();

		String memberRefreshToken = member.getRefreshToken();
		if (!memberRefreshToken.equals(refreshToken)) {
			throw new MemberCertifiedFailException(ErrorCode.NOT_VALID_REFRESH_TOKEN);
		}

		return createJwtReissueResponse(member, expert);
	}

	private MemberCertifiedResponse getMemberCertifiedResponse(Member member, Expert expert) {
		String memberEmail = member.getEmail();
		Long memberId = member.getId();
		Long expertId = expert.getId();

		String accessToken = jwtTokenProvider.createAccessToken(memberEmail, memberId, expertId);
		String refreshToken = jwtTokenProvider.createRefreshToken(memberEmail, memberId, expertId);

		member.refreshToken(refreshToken);

		return MemberCertifiedResponse.from(accessToken, refreshToken);
	}

	private UserDetails getCustomUserDetailsByRefreshToken(String refreshToken) {
		return jwtTokenProvider.getMemberAndExpertByRefreshToken(refreshToken);
	}

	private JwtReissueResponse createJwtReissueResponse(Member member, Expert expert) {
		Long memberId = member.getId();
		String memberEmail = member.getEmail();
		Long expertId = expert.getId();

		String reissuedAccessToken = jwtTokenProvider.createAccessToken(memberEmail, memberId, expertId);

		return new JwtReissueResponse(reissuedAccessToken);
	}
}
