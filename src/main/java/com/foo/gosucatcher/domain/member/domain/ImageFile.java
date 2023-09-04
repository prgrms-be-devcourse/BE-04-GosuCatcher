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

	private String path;
	private String fileName;
	private String fileExtension;

	@Builder
	public ImageFile(String path, String fileName, String fileExtension) {
		this.path = path;
		this.fileName = fileName;
		this.fileExtension = fileExtension;
	}
}
