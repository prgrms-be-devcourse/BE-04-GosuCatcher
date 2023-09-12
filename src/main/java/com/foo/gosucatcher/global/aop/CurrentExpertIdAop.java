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

@RequiredArgsConstructor
@Transactional
@Aspect
@Component
public class CurrentExpertIdAop {

	private static final String EXPERT_ID = "expertId";
	private final JwtTokenProvider jwtTokenProvider;

	@Around("@annotation(currentExpertId)")
	public Object getCurrentMemberId(ProceedingJoinPoint proceedingJoinPoint, CurrentExpertId currentExpertId) throws
		Throwable {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String token = jwtTokenProvider.resolveAccessToken(request);
		token = jwtTokenProvider.removeBearer(token);

		Authentication authentication = jwtTokenProvider.getAccessTokenAuthenticationByExpertId(token);
		Long expertId = Long.parseLong(authentication.getPrincipal().toString());

		Object[] modifiedArgs = modifyArgsWithMemberId(expertId, proceedingJoinPoint);

		return proceedingJoinPoint.proceed(modifiedArgs);
	}

	private Object[] modifyArgsWithMemberId(Long expertId, ProceedingJoinPoint proceedingJoinPoint) {
		Object[] parameters = proceedingJoinPoint.getArgs();

		MethodSignature signature = (MethodSignature)proceedingJoinPoint.getSignature();
		Method method = signature.getMethod();
		Parameter[] methodParameters = method.getParameters();

		for (int i = 0; i < methodParameters.length; i++) {
			String parameterName = methodParameters[i].getName();
			if (parameterName.equals(EXPERT_ID)) {
				parameters[i] = expertId;
			}
		}

		return parameters;
	}
}
