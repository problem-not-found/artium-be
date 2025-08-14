/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.ai.embedding.service;

public interface EmbeddingService {

  /**
   * 문자열 기반으로 임베딩하여 숫자 벡터로 생성해주는 메서드
   *
   * @param text 임베딩할 문자열
   * @return 임베딩된 숫자 벡터 배열
   */
  public float[] embed(String text);
}
