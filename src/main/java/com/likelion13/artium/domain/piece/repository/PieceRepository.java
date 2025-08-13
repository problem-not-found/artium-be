/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.piece.entity.SaveStatus;

public interface PieceRepository extends JpaRepository<Piece, Long> {

  @Query("SELECT p FROM Piece p WHERE p.user.id = :userId")
  Page<Piece> findByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query("SELECT p FROM Piece p WHERE p.user.id = :userId AND p.progressStatus IN :statuses")
  Page<Piece> findByUserIdAndProgressStatusIn(
      @Param("userId") Long userId,
      @Param("statuses") List<ProgressStatus> statuses,
      Pageable pageable);

  @Query("SELECT p FROM Piece p WHERE p.user.id = :userId AND p.saveStatus NOT IN :status")
  Page<Piece> findByUserIdAndSaveStatusNot(
      @Param("userId") Long userId, @Param("status") SaveStatus status, Pageable pageable);

  @Query("SELECT p FROM Piece p WHERE p.user.id = :userId AND p.saveStatus IN :status")
  Page<Piece> findByUserIdAndSaveStatus(
      @Param("userId") Long userId, @Param("status") SaveStatus status, Pageable pageable);

  Integer countByUserIdAndSaveStatus(Long userId, SaveStatus saveStatus);
}
