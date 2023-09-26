package com.foo.gosucatcher.domain.image.application.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.foo.gosucatcher.domain.review.domain.ReviewImage;

public record ImageDeleteRequest(

	List<String> filenames
) {

	public static ImageDeleteRequest from(List<ReviewImage> reviewImages) {
		List<String> filenames = new ArrayList<>();

		for (ReviewImage reviewImage : reviewImages) {
			filenames.add(reviewImage.getPath());
		}

		return new ImageDeleteRequest(filenames);
	}
}
