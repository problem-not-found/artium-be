/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  List<Review> findByExhibitionId(Long exhibitionId);
}
