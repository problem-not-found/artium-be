/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  Page<Review> findByExhibitionIdOrderByCreatedAtDesc(Long exhibitionId, Pageable pageable);
}
