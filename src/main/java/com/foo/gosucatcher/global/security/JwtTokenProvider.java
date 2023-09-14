package com.foo.gosucatcher.global.security;

import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.foo.gosucatcher.domain.expert.domain.Expert;
import com.foo.gosucatcher.domain.member.domain.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private final CustomUserDetailsService customUserDetailsService;

	private final long ACCESS_TOKEN_EXPIRED_TIME;
	private final long REFRESH_TOKEN_EXPIRED_TIME;
	private final String ACCESS_TOKEN_SECRET_KEY;
	private final String REFRESH_TOKEN_SECRET_KEY;

	public JwtTokenProvider(CustomUserDetailsService customUserDetailsService, JwtProperties jwtProperties) {
		this.customUserDetailsService = customUserDetailsService;
		this.ACCESS_TOKEN_EXPIRED_TIME = Duration.ofSeconds(jwtProperties.getAccessTokenExpiredTime()).toMillis();
		this.REFRESH_TOKEN_EXPIRED_TIME = Duration.ofDays(jwtProperties.getRefreshTokenExpiredTime()).toMillis();
		this.ACCESS_TOKEN_SECRET_KEY = getTokenSecretKey(jwtProperties.getAccessTokenSecretKey());
		this.REFRESH_TOKEN_SECRET_KEY = getTokenSecretKey(jwtProperties.getRefreshTokenSecretKey());
	}

	public String createAccessToken(String memberEmail, Long memberId, Long expertId) {
		return getToken(memberEmail, memberId, expertId, ACCESS_TOKEN_EXPIRED_TIME, ACCESS_TOKEN_SECRET_KEY);
	}

	public String createRefreshToken(String memberEmail, Long memberId, Long expertId) {
		return getToken(memberEmail, memberId, expertId, REFRESH_TOKEN_EXPIRED_TIME, REFRESH_TOKEN_SECRET_KEY);
	}

	public Authentication getAccessTokenAuthenticationByMemberEmail(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberEmail(token, ACCESS_TOKEN_SECRET_KEY));
		String email = userDetails.getUsername();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(email, password, authorities);
	}

	public Authentication getAccessTokenAuthenticationByMemberId(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberId(token, ACCESS_TOKEN_SECRET_KEY));
		Long id = ((Member)userDetails).getId();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(id, password, authorities);
	}

	public Authentication getAccessTokenAuthenticationByExpertId(String token) {
		CustomUserDetails customUserDetails = customUserDetailsService.loadMemberAndExpertByMemberId(
			getExpertId(token, ACCESS_TOKEN_SECRET_KEY));
		Expert expert = customUserDetails.getExpert();
		Member member = customUserDetails.getMember();

		Long id = expert.getId();
		String password = member.getPassword();
		Collection<? extends GrantedAuthority> authorities = member.getAuthorities();

		return new UsernamePasswordAuthenticationToken(id, password, authorities);
	}

	public Authentication getRefreshTokenAuthentication(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberEmail(token, REFRESH_TOKEN_SECRET_KEY));
		String email = userDetails.getUsername();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(email, password, authorities);
	}

	public String resolveAccessToken(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}

	public String resolveRefreshToken(HttpServletRequest request) {
		return request.getHeader("RefreshToken");
	}

	public boolean isValidAccessToken(String token) {
		return isValidToken(token, ACCESS_TOKEN_SECRET_KEY);
	}

	public boolean isValidRefreshToken(String token) {
		return isValidToken(token, REFRESH_TOKEN_SECRET_KEY);
	}

	public String removeBearer(String token) {
		return token.substring("Bearer ".length());
	}

	private String getToken(String memberEmail, Long memberId, Long expertId, long tokenExpiredTime, String secretKey) {
		Date date = new Date();

		Claims claims = Jwts.claims()
			.setSubject("GosuCatcher");
		claims.put("memberEmail", memberEmail);
		claims.put("memberId", memberId);
		claims.put("expertId", expertId);

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setClaims(claims)
			.setIssuedAt(date)
			.setIssuer("GosuCatcher-server")
			.setExpiration(new Date(date.getTime() + tokenExpiredTime))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	private String getMemberEmail(String token, String secretKey) {
		return Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("memberEmail")
			.toString();
	}

	private Long getMemberId(String token, String secretKey) {
		String memberId = Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("memberId")
			.toString();

		return Long.parseLong(memberId);
	}

	private Long getExpertId(String token, String secretKey) {
		String expertId = Jwts.parser()
			.setSigningKey(secretKey)
			.parseClaimsJws(token)
			.getBody()
			.get("expertId")
			.toString();

		return Long.parseLong(expertId);
	}

	private boolean isValidToken(String token, String secretKey) {
		token = removeBearer(token);
		try {
			Jws<Claims> claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token);

			return !claims.getBody()
				.getExpiration()
				.before(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isRefreshTokenExistInDb(String refreshToken) {
		Long memberId = getMemberId(refreshToken, REFRESH_TOKEN_SECRET_KEY);
		log.warn("isRefreshTokenExistInDb : {}", memberId);
		return customUserDetailsService.isSameRefreshTokenExist(memberId, refreshToken);
	}

	public CustomUserDetails getMemberAndExpertByRefreshToken(String refreshToken) {
		Long memberId = getMemberId(refreshToken, REFRESH_TOKEN_SECRET_KEY);

		return customUserDetailsService.loadMemberAndExpertByMemberId(memberId);
	}

	private String getTokenSecretKey(String accessTokenSecretKey) {
		return Base64.getEncoder()
			.encodeToString(accessTokenSecretKey.getBytes());
	}
}
