package com.foo.gosucatcher.domain.member.domain;

import javax.persistence.Embeddable;

import com.foo.gosucatcher.domain.image.domain.Image;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberImage extends Image {

	private static final String DEFAULT_PATH = "https://gosu-catcher.s3.ap-northeast-2.amazonaws.com/default.png";

	public MemberImage(String fileName) {
		super(fileName);
	}

	public boolean isDefaultImagePath() {
		return filename.equals(DEFAULT_PATH);
	}

	public void changePathToDefault() {
		filename = DEFAULT_PATH;
	}
}
