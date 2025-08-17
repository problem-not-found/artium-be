/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceLike.entity.PieceLike;

public interface PieceLikeRepository extends JpaRepository<PieceLike, Long> {

  Optional<PieceLike> findByUser_IdAndPiece_Id(Long userId, Long pieceId);

  @Query("SELECT pl.piece.id from PieceLike pl where pl.user.id= :userId")
  List<Long> findIdsByUser_Id(@Param("userId") Long userId);

  @Query("SELECT pl.piece from PieceLike pl where pl.user.id= :userId")
  Page<Piece> findPieceByUser_Id(@Param("userId") Long userId, Pageable pageable);
}
