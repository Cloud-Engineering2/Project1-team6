package com.cloud6.place.service;

import org.springframework.stereotype.Service;

import com.cloud6.place.entity.Place;
import com.cloud6.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	 private final PlaceRepository placeRepository;


	    public Place getFirstPlace() {
	        return placeRepository.findById(1).orElse(null);
	    }

}
