package com.cloud6.place.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud6.place.entity.Place;
import com.cloud6.place.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
	private final PlaceService placeService;

	@GetMapping
	public ResponseEntity<List<Place>> getAllPlaces() {
		try {
			// PlaceService에서 모든 장소를 가져옴
			List<Place> places = placeService.getAllPlaces();
			return ResponseEntity.ok(places); // HTTP 200 OK 응답과 함께 장소 목록 반환
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 오류 발생 시 500 반환
		}
	}
	@GetMapping("/{pid}")
    public ResponseEntity<Place> getDetailPlace(@PathVariable Long pid) {
        try {
            // PlaceService에서 pid에 해당하는 장소를 가져옴
            Place place = placeService.getPlaceById(pid);
            if (place != null) {
                return ResponseEntity.ok(place); // HTTP 200 OK 응답과 함께 장소 반환
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 장소가 없으면 404 반환
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 오류 발생 시 500 반환
        }
    }
	
}
