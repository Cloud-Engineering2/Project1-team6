package com.cloud6.place.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long placeId;
    private Long userId;
    private Byte rating;
    private String comment;
}
