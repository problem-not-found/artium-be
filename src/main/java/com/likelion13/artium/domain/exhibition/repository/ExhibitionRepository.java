/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

  @Query("SELECT e FROM Exhibition e JOIN e.exhibitionUsers eu WHERE eu.user.id = :userId")
  Page<Exhibition> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
