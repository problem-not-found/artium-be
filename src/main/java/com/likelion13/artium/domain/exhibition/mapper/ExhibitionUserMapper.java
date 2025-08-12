/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.exhibition.mapper;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.exhibition.mapping.ExhibitionUser;
import com.likelion13.artium.domain.user.entity.User;

@Component
public class ExhibitionUserMapper {

  public ExhibitionUser toExhibitionUser(Exhibition exhibition, User currentUser) {
    return ExhibitionUser.builder()
        .exhibition(exhibition)
        .user(currentUser)
        .fillAll(validateExhibitionFields(exhibition))
        .build();
  }

  private boolean validateExhibitionFields(Exhibition exhibition) {
    return exhibition.getThumbnailImageUrl() != null
        && exhibition.getTitle() != null
        && exhibition.getDescription() != null
        && exhibition.getStartDate() != null
        && exhibition.getEndDate() != null
        && exhibition.getAddress() != null
        && exhibition.getOfflineDescription() != null
        && exhibition.getAccountNumber() != null
        && exhibition.getBankName() != null
        && exhibition.getExhibitionStatus() != null;
  }
}
