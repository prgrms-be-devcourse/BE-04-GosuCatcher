package com.foo.gosucatcher.global.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

		if (accessToken != null) {
			log.warn("액세스 토큰 : {}", accessToken);
			if (jwtTokenProvider.isValidAccessToken(accessToken)) {
				accessToken = jwtTokenProvider.removeBearer(accessToken);
				Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberEmail(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else if (!jwtTokenProvider.isValidAccessToken(accessToken) && refreshToken != null) {
				log.warn("되냐?");
				log.warn("리플레시 토큰 : {}", refreshToken);
				boolean isValidRefreshToken = jwtTokenProvider.isValidRefreshToken(refreshToken);
				log.warn("리플레시 유효함? : {}", isValidRefreshToken);
				boolean isRefreshTokenExistInDb = jwtTokenProvider.isRefreshTokenExistInDb(refreshToken);
				log.warn("리플레시 디비에 있음? : {}", isRefreshTokenExistInDb);
				if (isValidRefreshToken && isRefreshTokenExistInDb) {
					CustomUserDetails customDetails = jwtTokenProvider.getMemberAndExpertByRefreshToken(refreshToken);

					Member member = customDetails.getMember();
					Expert expert = customDetails.getExpert();
					String newAccessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getId(),
						expert.getId());
					log.warn("member : {}, expert : {}, newAccessToken : {}", member, expert, newAccessToken);
					Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberId(
						newAccessToken);
					log.warn("authentication : {}", authentication.toString());
					SecurityContextHolder.getContext().setAuthentication(authentication);

					filterChain.doFilter(request, response);
				}

				filterChain.doFilter(request, response);
			}
		}

		// try {
		// 	if (accessToken != null) {
		// 		if (jwtTokenProvider.isValidAccessToken(accessToken)) {
		// 			accessToken = jwtTokenProvider.removeBearer(accessToken);
		// 			Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberEmail(
		// 				accessToken);
		// 			SecurityContextHolder.getContext().setAuthentication(authentication);
		// 		} else if (!jwtTokenProvider.isValidAccessToken(accessToken) && refreshToken != null) {
		// 			boolean isValidRefreshToken = jwtTokenProvider.isValidRefreshToken(refreshToken);
		// 			boolean isRefreshTokenExistInDb = jwtTokenProvider.isRefreshTokenExistInDb(refreshToken);
		//
		// 			if (isValidRefreshToken && isRefreshTokenExistInDb) {
		// 				CustomUserDetails customDetails = jwtTokenProvider.getMemberAndExpertByRefreshToken(
		// 					refreshToken);
		//
		// 				Member member = customDetails.getMember();
		// 				Expert expert = customDetails.getExpert();
		// 				String newAccessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getId(),
		// 					expert.getId());
		// 				log.warn("기존 토큰 만료됐음 ㅎㅎ 재발생해드림~ 헤더 확인해보삼");
		// 				Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberId(
		// 					newAccessToken);
		// 				SecurityContextHolder.getContext().setAuthentication(authentication);
		// 			}
		// 		}
		// 	}
		//
		filterChain.doFilter(request, response);
		// } catch (ExpiredJwtException e) {
		// 	response.setStatus(401);
		// 	response.setContentType("application/json");
		// 	response.setCharacterEncoding("utf-8");
		// 	ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EXPIRED_AUTHENTICATION);
		//
		// 	new ObjectMapper().writeValue(response.getWriter(), errorResponse);
		// }
	}
}
