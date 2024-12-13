package com.cloud6.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long placeId;  // 리뷰가 속한 장소의 ID
    private Byte rating;   // 별점 (1 ~ 5)
    private String comment;  // 리뷰 내용
}
