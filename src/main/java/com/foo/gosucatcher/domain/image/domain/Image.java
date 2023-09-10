package com.foo.gosucatcher.domain.image.domain;

import javax.persistence.MappedSuperclass;

import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class Image {

	private String filename;
	private String url;
	private Long size;

	public Image(String filename, String url, Long size) {
		this.filename = filename;
		this.url = url;
		this.size = size;
	}

	public Image() {
	}
}
