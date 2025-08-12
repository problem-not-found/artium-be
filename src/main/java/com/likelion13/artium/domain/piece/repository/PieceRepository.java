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

public interface PieceRepository extends JpaRepository<Piece, Long> {

  @Query("SELECT p FROM Piece p WHERE p.user.id = :userId")
  Page<Piece> findByUserId(@Param("userId") Long userId, Pageable pageable);

  @Query(
      "SELECT p FROM Piece p WHERE p.user.id = :userId AND p.status IN ("
          + "com.likelion13.artium.domain.piece.entity.Status.REGISTERED, "
          + "com.likelion13.artium.domain.piece.entity.Status.ON_DISPLAY)")
  Page<Piece> findByUserIdAndStatusRegisteredOrOnDisplay(
      @Param("userId") Long userId, Pageable pageable);
}
