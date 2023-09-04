package com.foo.gosucatcher.domain.member.domain;

import org.springframework.web.multipart.MultipartFile;

public interface MemberProfileRepository {

	void initializeMemberProfile(Member member);

	ImageFile uploadImage(Member member, MultipartFile file);

	ImageFile findImage(Member member);

	void deleteImage(Member member);
}
