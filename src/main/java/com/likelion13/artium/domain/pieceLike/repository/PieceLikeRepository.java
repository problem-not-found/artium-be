/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.pieceLike.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.pieceLike.entity.PieceLike;

public interface PieceLikeRepository extends JpaRepository<PieceLike, Long> {

  Optional<PieceLike> findByUser_IdAndPiece_Id(Long userId, Long pieceId);
}
