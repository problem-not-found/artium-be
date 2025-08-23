/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.qdrant.service;

import java.util.*;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.qdrant.entity.CollectionName;

public interface QdrantService {

  /** 모든 컬렉션 생성 메서드 (해당 컬렉션이 없으면 생성, 있으면 통과) */
  void ensureAllCollections();

  /**
   * 컬렉션에 특정 객체의 벡터값을 기준으로 업서트 할 수 있는 메서드
   *
   * @param id 식별자
   * @param vector 작품의 원하는 값으로 임베딩 된 벡터값
   * @param piece 작품 객체
   * @param collectionName 업서트 할 위치(Qdrant의 컬렉션 명)
   */
  void upsertPiecePoint(Long id, float[] vector, Piece piece, CollectionName collectionName);

  /**
   * 컬렉션에 특정 객체의 벡터값을 기준으로 업서트 할 수 있는 메서드
   *
   * @param id 식별자
   * @param vector 전시의 원하는 값으로 임베딩 된 벡터값
   * @param exhibition 전시 객체
   * @param collectionName 업서트 할 위치(Qdrant의 컬렉션 명)
   */
  void upsertExhibitionPoint(
      Long id, float[] vector, Exhibition exhibition, CollectionName collectionName);

  /**
   * 컬렉션에 특정 객체의 벡터값을 기준으로 업서트 할 수 있는 메서드
   *
   * @param id 식별자
   * @param vector 유저의 관심사를 기준으로 임베딩 된 벡터값
   * @param user 유저 객체
   * @param collectionName 업서트 할 위치(Qdrant의 컬렉션 명)
   */
  void upsertUserPoint(Long id, float[] vector, User user, CollectionName collectionName);

  /**
   * 포인트 모음(points)에 식별자, 벡터값, 페이로드를 기반으로 한 포인트를 하나 갱신(또는 삽입)
   *
   * @param id 식별자
   * @param vector 벡터값
   * @param payload 추가 속성 값들
   * @param collectionName 포인트를 넣을 컬렉션 이름
   */
  void upsertPoint(
      Long id, float[] vector, Map<String, Object> payload, CollectionName collectionName);

  /**
   * 식별자를 받아 해당 식별자의 벡터값을 반환
   *
   * @param id 식별자
   * @param collectionName 찾을 컬렉션 이름
   * @return 벡터값 반환
   */
  float[] retrieveVectorById(Long id, CollectionName collectionName);


  /**
   * @param query
   * @param limit
   * @param excludeArtworkIds
   * @param collectionName
   * @return
   */
  List<Map<String, Object>> search(
      float[] query, int limit, List<Long> excludeArtworkIds, CollectionName collectionName);
}
