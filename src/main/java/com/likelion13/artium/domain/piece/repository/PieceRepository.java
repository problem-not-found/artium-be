/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.piece.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.piece.entity.Piece;

public interface PieceRepository extends JpaRepository<Piece, Long> {

  List<Piece> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}
