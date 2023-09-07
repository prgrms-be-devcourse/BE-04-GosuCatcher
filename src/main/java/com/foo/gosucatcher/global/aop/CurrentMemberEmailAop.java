package com.foo.gosucatcher.global.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.foo.gosucatcher.global.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Aspect
@Component
public class CurrentMemberEmailAop {

	private static final String MEMBER_ID = "memberEmail";
	private final JwtTokenProvider jwtTokenProvider;

	@Around("@annotation(currentMemberEmail)")
	public Object getCurrentMemberEmail(ProceedingJoinPoint proceedingJoinPoint,
		CurrentMemberEmail currentMemberEmail) throws
		Throwable {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String token = jwtTokenProvider.resolveAccessToken(request);
		token = jwtTokenProvider.bearerRemove(token);

		Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByMemberEmail(token);
		String memberEmail = authentication.getPrincipal().toString();

		Object[] modifiedArgs = modifyArgsWithMemberEmail(memberEmail, proceedingJoinPoint);

		return proceedingJoinPoint.proceed(modifiedArgs);
	}

	private Object[] modifyArgsWithMemberEmail(String memberEmail, ProceedingJoinPoint proceedingJoinPoint) {
		Object[] parameters = proceedingJoinPoint.getArgs();

		MethodSignature signature = (MethodSignature)proceedingJoinPoint.getSignature();
		Method method = signature.getMethod();
		Parameter[] methodParameters = method.getParameters();

		for (int i = 0; i < methodParameters.length; i++) {
			String parameterName = methodParameters[i].getName();
			if (parameterName.equals(MEMBER_ID)) {
				parameters[i] = memberEmail;
			}
		}

		return parameters;
	}
}
