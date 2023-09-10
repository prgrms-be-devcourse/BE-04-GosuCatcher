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

public class JwtAccessTokenFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	public JwtAccessTokenFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String token = jwtTokenProvider.resolveAccessToken(request);

		try {
			if (token != null && jwtTokenProvider.isValidAccessToken(token)) {
				token = jwtTokenProvider.removeBearer(token);
				Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberEmail(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			response.setStatus(401);
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");

			ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
			new ObjectMapper().writeValue(response.getWriter(), errorResponse);
		}
	}
}
