/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.page.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.dto.response.ExhibitionResponse;
import com.likelion13.artium.global.page.response.PageResponse;

@Component
public class PageMapper {

  private <T> PageResponse<T> toPageResponse(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .pageNum(page.getNumber())
        .pageSize(page.getSize())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }

  public PageResponse<ExhibitionResponse> toExhibitionPageResponse(Page<ExhibitionResponse> page) {
    return toPageResponse(page);
  }
}
