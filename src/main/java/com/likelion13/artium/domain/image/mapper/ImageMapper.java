/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.image.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion13.artium.domain.image.dto.response.ImageResponse;
import com.likelion13.artium.domain.image.entity.Image;

@Component
public class ImageMapper {

  public ImageResponse toImageResponse(List<Image> images) {
    return ImageResponse.builder()
        .imagesUrl(images.stream().map(Image::getImageUrl).toList())
        .build();
  }
}
