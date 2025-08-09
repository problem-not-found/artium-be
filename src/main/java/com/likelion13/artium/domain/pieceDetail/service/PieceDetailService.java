package com.likelion13.artium.domain.pieceDetail.service;

import com.likelion13.artium.domain.pieceDetail.repository.PieceDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PieceDetailService {

    private final PieceDetailRepository pieceDetailRepository;
}
