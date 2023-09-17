package com.foo.gosucatcher.domain.image.domain;

import javax.persistence.MappedSuperclass;

import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class Image {

	protected String filename;

	public Image(String filename) {
		this.filename = filename;
	}

	public Image() {
	}
}
