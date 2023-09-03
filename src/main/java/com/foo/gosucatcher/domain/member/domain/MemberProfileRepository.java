package com.foo.gosucatcher.domain.member.domain;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileRepository {
	String uploadProfileImage(long memberId, MultipartFile file);

	Resource findProfileImage(Member member);
}
