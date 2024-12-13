package com.cloud6.place.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cloud6.place.entity.Review;
import com.cloud6.place.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

    public List<Review> getReviewsByPlaceId(Long placeId) {
        return reviewRepository.findByPlacePlaceId(placeId);  // ReviewRepository에서 placeId로 리뷰 조회
    }

	public Review saveReview(Review review) {
		return reviewRepository.save(review);	
	}

}
