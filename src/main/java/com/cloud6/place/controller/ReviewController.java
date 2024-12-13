package com.cloud6.place.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud6.place.dto.ReviewDTO;
import com.cloud6.place.entity.Place;
import com.cloud6.place.entity.Review;
import com.cloud6.place.entity.User;
import com.cloud6.place.repository.ReviewRepository;
import com.cloud6.place.repository.UserRepository;
import com.cloud6.place.security.JwtTokenProvider;
import com.cloud6.place.service.PlaceService;
import com.cloud6.place.service.ReviewService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
	
	private final ReviewService reviewService;
	private final PlaceService placeService;
	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
	@GetMapping("/{id}")
	public ResponseEntity<List<Review>> getReviewsByPlaceId(@PathVariable Long id) {
	    try {
	        List<Review> reviews = reviewService.getReviewsByPlaceId(id); // id로 리뷰 조회
	        if (reviews.isEmpty()) {
	            return ResponseEntity.noContent().build(); // 리뷰가 없으면 204 No Content 응답
	        }
	        return ResponseEntity.ok(reviews); // 리뷰가 있으면 200 OK 응답과 함께 반환
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(null); // 오류 발생 시 500 서버 에러 응답
	    }
	}

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO, HttpServletRequest request) {
        // 쿠키에서 토큰 추출
        Cookie[] cookies = request.getCookies();
        String token = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();  // "token" 쿠키에서 값 추출
                    break;
                }
            }
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 유효하지 않습니다.");
        }

        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 토큰을 통해 userId 추출 (JWT 디코딩)
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 유효하지 않습니다.");
        }

        // Place 조회
        Place place = placeService.getPlaceById(reviewDTO.getPlaceId());
        if (place == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Place not found.");
        }

        // 리뷰 저장
        Review review = new Review();
        review.setPlace(place);
        review.setUser(user);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        // 수정 : 저장된 리뷰 이후, 해당 장소의 최신 리뷰 목록 반환
        List<Review> updatedReviews = reviewService.getReviewsByPlaceId(reviewDTO.getPlaceId());

        return ResponseEntity.status(HttpStatus.CREATED).body(updatedReviews);
     // 기존 : return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    // 추가 : 특정 reviewId에 해당하는 리뷰를 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewDTO reviewDTO, HttpServletRequest request) {
        // 쿠키에서 토큰 추출 및 검증
    	System.out.println(reviewId);
    	System.out.println(reviewDTO);
        String token = getTokenFromCookies(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 사용자 확인
        String username = jwtTokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 유효하지 않습니다.");
        }

        // 리뷰 조회 및 작성자 확인
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null || !review.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }

        // 리뷰 업데이트
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.save(review);

        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, 
                                          @RequestParam String username, 
                                          HttpServletRequest request) {
        // 사용자 확인 및 리뷰 확인
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보가 유효하지 않습니다.");
        }

        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null || !review.getUser().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        // 리뷰 삭제
        reviewRepository.delete(review);

        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }
    
    // 코드 중복 방지를 위해, 쿠키에서 JWT 토큰을 추출하는 로직을 별도 메서드로 분리
    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
