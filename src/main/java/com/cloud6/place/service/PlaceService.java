package com.cloud6.place.service;

import java.util.List;


import org.springframework.stereotype.Service;

import com.cloud6.place.entity.Place;
import com.cloud6.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;

	public List<Place> getAllPlaces() {
		return placeRepository.findAll();
	}

	public Place getPlaceById(Long pid) {
		return placeRepository.findById(pid).orElse(null);
	}

    public Place findById(Long placeId) {
        return placeRepository.findById(placeId).orElse(null); // 없으면 null 반환
    }

}
