/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionLike;
import com.likelion13.artium.domain.user.entity.User;

public interface ExhibitionLikeRepository extends JpaRepository<ExhibitionLike, Long> {

  Optional<ExhibitionLike> findByExhibitionAndUser(Exhibition exhibition, User user);

}
