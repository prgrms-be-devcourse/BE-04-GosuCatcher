package com.foo.gosucatcher.domain.image.application.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.foo.gosucatcher.domain.review.domain.Review;
import com.foo.gosucatcher.domain.review.domain.ReviewImage;

public record ImagesResponse(

	List<String> filenames
) {

	public static ImagesResponse from(List<String> filenames) {
		return new ImagesResponse(filenames);
	}

	public static List<ReviewImage> toReviewImages(Review review, ImagesResponse imagesResponse) {
		List<ReviewImage> reviewImages = new ArrayList<>();

		for (String filename : imagesResponse.filenames()) {
			ReviewImage reviewImage = ReviewImage.builder()
				.path(filename)
				.review(review)
				.build();

			reviewImages.add(reviewImage);
		}

		return reviewImages;
	}
}
