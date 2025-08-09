package com.likelion13.artium.domain.piece.mapper;


import static java.util.stream.Collectors.toList;

import com.likelion13.artium.domain.piece.dto.response.PieceResponse;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.pieceDetail.entity.PieceDetail;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PieceMapper {

    public PieceResponse toPieceResponse(Piece piece) {
        return PieceResponse.builder()
            .pieceId(piece.getId())
            .title(piece.getTitle())
            .description(piece.getDescription())
            .imageUrl(piece.getImageUrl())
            .isPurchasable(piece.getIsPurchasable())
            .userId(piece.getUser().getId())
            .pieceDetails(
                piece.getPieceDetails() == null ? List.of()
                : piece.getPieceDetails().stream()
            .map(PieceDetail::getImageUrl)
            .toList())
            .build();
    }
}
