/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13.artium.domain.exhibition.mapping.ExhibitionParticipant;

public interface ExhibitionParticipantRepository
    extends JpaRepository<ExhibitionParticipant, Long> {}
