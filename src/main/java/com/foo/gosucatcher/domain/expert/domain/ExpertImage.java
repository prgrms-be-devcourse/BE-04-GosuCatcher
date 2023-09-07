package com.foo.gosucatcher.domain.expert.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.foo.gosucatcher.domain.image.domain.Image;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expert_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpertImage extends Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "expert_id")
	private Expert expert;

	@Builder
	public ExpertImage(String filename, String url, Long size, Expert expert) {
		super(filename, url, size);
		this.expert = expert;
	}
}
