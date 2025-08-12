/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

  List<Exhibition> findByUserIdAndFillAll(Long userId, boolean fillAll);

  Page<Exhibition> findByUserIdAndFillAll(Long userId, Boolean fillAll, Pageable pageable);

  @Query(
      "SELECT e FROM Exhibition e LEFT JOIN e.exhibitionLikes el WHERE e.fillAll = true GROUP BY e.id ORDER BY COUNT(el) DESC")
  Page<Exhibition> findAllOrderByLikesCountDesc(Pageable pageable);

  @Query(
      "SELECT e FROM Exhibition e WHERE e.startDate > :startDate AND e.exhibitionStatus = :status AND e.fillAll = true ORDER BY e.endDate ASC")
  Page<Exhibition> findRecentOngoingExhibitions(
      @Param("startDate") LocalDate startDate,
      @Param("status") ExhibitionStatus exhibitionStatus,
      Pageable pageable);
}
