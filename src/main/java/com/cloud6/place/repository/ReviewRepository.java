package com.cloud6.place.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud6.place.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>  {

	List<Review> findByPlacePlaceIdOrderByCreatedAtDesc(Long placeId); // 수정 : 최신 순 정렬
	
// 기존:	List<Review> findByPlacePlaceId(Long placeId);

}

