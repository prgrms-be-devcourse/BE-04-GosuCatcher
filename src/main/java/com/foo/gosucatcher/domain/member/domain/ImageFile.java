package com.foo.gosucatcher.domain.member.domain;

import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

	private static final String DEFAULT_PATH = "/Users/ysng_ysng/Desktop/projects/GosuCatcher/static/image/default.png";
	private String path;
	private String fileName;
	private String fileExtension;

	@Builder
	public ImageFile(String path, String fileName, String fileExtension) {
		this.path = path;
		this.fileName = fileName;
		this.fileExtension = fileExtension;
	}

	public boolean isDefaultImagePath() {
		return path.equals(DEFAULT_PATH);
	}

	public void changePathToDefault() {
		this.path = DEFAULT_PATH;
	}
}
