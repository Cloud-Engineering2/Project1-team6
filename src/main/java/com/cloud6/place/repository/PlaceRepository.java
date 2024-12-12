package com.cloud6.place.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud6.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Integer> {
	
}
