package com.foo.gosucatcher.global.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foo.gosucatcher.global.error.ErrorCode;
import com.foo.gosucatcher.global.error.ErrorResponse;

import io.jsonwebtoken.ExpiredJwtException;
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

		try {
			if (accessToken != null && jwtTokenProvider.isValidAccessToken(accessToken)) {
				accessToken = jwtTokenProvider.removeBearer(accessToken);
				Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberEmail(accessToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			if (refreshToken != null && jwtTokenProvider.isValidRefreshToken(refreshToken)) {
				refreshToken = jwtTokenProvider.removeBearer(refreshToken);
				Authentication authentication = jwtTokenProvider.getRefreshTokenAuthentication(refreshToken);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			response.setStatus(401);
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");

			ErrorResponse errorResponse;
			if (jwtTokenProvider.isValidAccessToken(accessToken)) {
				errorResponse = ErrorResponse.of(ErrorCode.EXPIRED_AUTHENTICATION);
			} else {
				errorResponse = ErrorResponse.of(ErrorCode.NOT_VALID_REFRESH_TOKEN);
			}
			new ObjectMapper().writeValue(response.getWriter(), errorResponse);
		}
	}
}
