package com.foo.gosucatcher.global.security;

import java.time.Duration;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
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

@Component
public class JwtTokenProvider {

	private static final long ACCESS_TOKEN_EXPIRED_TIME = Duration.ofHours(24).toMillis();
	private static final long REFRESH_TOKEN_EXPIRED_TIME = Duration.ofDays(10).toMillis();

	private final String accessTokenSecretKey;
	private final String refreshTokenSecretKey;

	private final CustomUserDetailsService customUserDetailsService;

	public JwtTokenProvider(
		@Value("${spring.jwt.accessTokenSecretKey}")
		String accessTokenSecretKey,
		@Value("${spring.jwt.refreshTokenSecretKey}")
		String refreshTokenSecretKey,
		CustomUserDetailsService customUserDetailsService
	) {
		this.accessTokenSecretKey = getTokenSecretKey(accessTokenSecretKey);
		this.refreshTokenSecretKey = getTokenSecretKey(refreshTokenSecretKey);
		this.customUserDetailsService = customUserDetailsService;
	}

	public String createAccessToken(String memberEmail, Long memberId, Long expertId) {
		return getToken(memberEmail, memberId, expertId, ACCESS_TOKEN_EXPIRED_TIME, accessTokenSecretKey);
	}

	public String createRefreshToken(String memberEmail, Long memberId, Long expertId) {
		return getToken(memberEmail, memberId, expertId, REFRESH_TOKEN_EXPIRED_TIME, refreshTokenSecretKey);
	}

	public Authentication getAccessTokenAuthenticationByMemberEmail(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberEmail(token, accessTokenSecretKey));
		String email = userDetails.getUsername();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(email, password, authorities);
	}

	public Authentication getAccessTokenAuthenticationByMemberId(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberId(token, accessTokenSecretKey));
		Long id = ((Member)userDetails).getId();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(id, password, authorities);
	}

	public Authentication getAccessTokenAuthenticationByExpertId(String token) {
		CustomUserDetails customUserDetails = customUserDetailsService.loadExpertByMemberId(
			getExpertId(token, accessTokenSecretKey));
		Expert expert = customUserDetails.getExpert();
		Member member = customUserDetails.getMember();

		Long id = expert.getId();
		String password = member.getPassword();
		Collection<? extends GrantedAuthority> authorities = member.getAuthorities();

		return new UsernamePasswordAuthenticationToken(id, password, authorities);
	}

	public Authentication getRefreshTokenAuthentication(String token) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(
			getMemberEmail(token, refreshTokenSecretKey));
		String email = userDetails.getUsername();
		String password = userDetails.getPassword();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

		return new UsernamePasswordAuthenticationToken(email, password, authorities);
	}

	public String resolveAccessToken(HttpServletRequest request) {
		return request.getHeader("AccessToken");
	}

	public boolean isValidAccessToken(String token) {
		return isValidToken(token, accessTokenSecretKey);
	}

	public boolean isValidRefreshToken(String token) {
		return isValidToken(token, refreshTokenSecretKey);
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

	private String getTokenSecretKey(String accessTokenSecretKey) {
		return Base64.getEncoder()
			.encodeToString(accessTokenSecretKey.getBytes());
	}
}
