/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.SaveStatus;

public interface PieceRepository extends JpaRepository<Piece, Long> {

  @Query("SELECT p FROM Piece p WHERE p.user.id = :userId")
  Page<Piece> findByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query(
      "SELECT p FROM Piece p WHERE p.user.id = :userId AND p.progressStatus IN ("
          + "com.likelion13.artium.domain.piece.entity.ProgressStatus.REGISTERED, "
          + "com.likelion13.artium.domain.piece.entity.ProgressStatus.ON_DISPLAY)")
  Page<Piece> findByUserIdAndProgressStatusRegisteredOrOnDisplay(
      @Param("userId") Long userId, Pageable pageable);

  @Query(
      "SELECT p FROM Piece p WHERE p.user.id = :userId AND p.saveStatus NOT IN ("
          + "com.likelion13.artium.domain.piece.entity.SaveStatus.DRAFT)")
  Page<Piece> findByUserIdAndSaveStatusNotDraft(@Param("userId") Long userId, Pageable pageable);

  @Query(
      "SELECT p FROM Piece p WHERE p.user.id = :userId AND p.saveStatus IN ("
          + "com.likelion13.artium.domain.piece.entity.SaveStatus.DRAFT)")
  Page<Piece> findByUserIdAndSaveStatusDraft(@Param("userId") Long userId, Pageable pageable);

  Integer countByUserIdAndSaveStatus(Long userId, SaveStatus saveStatus);
}
