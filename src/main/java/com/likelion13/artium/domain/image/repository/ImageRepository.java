/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {}
