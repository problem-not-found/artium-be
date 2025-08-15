/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.qdrant.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.likelion13.artium.domain.exhibition.entity.Exhibition;
import com.likelion13.artium.domain.piece.entity.Piece;
import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.global.config.QdrantConfig;
import com.likelion13.artium.global.exception.CustomException;
import com.likelion13.artium.global.qdrant.entity.CollectionName;
import com.likelion13.artium.global.qdrant.exception.QdrantErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class QdrantServiceImpl implements QdrantService {

  private final WebClient qdrantWebClient;
  private final QdrantConfig qdrantConfig;

  @PostConstruct
  @Override
  public void ensureAllCollections() {
    for (CollectionName collectionName : CollectionName.values()) {
      createIfAbsent(collectionName);
    }
  }

  @Override
  public void upsertPiecePoint(
      Long id, float[] vector, Piece piece, CollectionName collectionName) {
    upsertPoint(
        id,
        vector,
        Map.of(
            "pieceId", piece.getId(),
            "lang", "ko",
            "createdAt", piece.getCreatedAt().toString()),
        collectionName);
  }

  @Override
  public void upsertExhibitionPoint(
      Long id, float[] vector, Exhibition exhibition, CollectionName collectionName) {
    upsertPoint(
        id,
        vector,
        Map.of(
            "exhibitionId", exhibition.getId(),
            "lang", "ko",
            "createdAt", exhibition.getCreatedAt().toString()),
        collectionName);
  }

  @Override
  public void upsertUserPoint(Long id, float[] vector, User user, CollectionName collectionName) {
    upsertPoint(
        id,
        vector,
        Map.of(
            "userId", user.getId(),
            "lang", "ko",
            "createdAt", user.getCreatedAt().toString()),
        collectionName);
  }

  @Override
  public void upsertPoint(
      Long id, float[] vector, Map<String, Object> payload, CollectionName collectionName) {
    if (vector.length != qdrantConfig.getVectorSize())
      throw new CustomException(QdrantErrorCode.VECTOR_SIZE_MISMATCH);

    Map<String, Object> point = new HashMap<>();
    point.put("id", id);
    point.put("vector", vector);
    point.put("payload", payload);

    Map<String, Object> body = Map.of("points", List.of(point));

    qdrantWebClient
        .put()
        .uri("/collections/{name}/points?wait=true", getPrefix(collectionName))
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  @Override
  public float[] retrieveVectorById(Long id, CollectionName collectionName) {
    if (id == null) return null;

    Map resp =
        qdrantWebClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/collections/{name}/points/{id}")
                        .queryParam("with_vector", true)
                        .queryParam("with_payload", false)
                        .build(getPrefix(collectionName), id))
            .retrieve()
            .onStatus(
                s -> s.is4xxClientError() || s.is5xxServerError(),
                r ->
                    r.bodyToMono(String.class)
                        .map(msg -> new CustomException(QdrantErrorCode.VECTOR_SIZE_MISMATCH)))
            .bodyToMono(Map.class)
            .block();

    Map<String, Object> result = (Map<String, Object>) resp.get("result");

    List<Double> v = (List<Double>) result.get("vector");

    float[] f = new float[v.size()];
    for (int i = 0; i < v.size(); i++) f[i] = v.get(i).floatValue();

    return f;
  }

  @Override
  public List<float[]> retrieveVectorsByIds(List<Long> ids, CollectionName collectionName) {
    if (ids.isEmpty()) return List.of();

    Map<String, Object> body =
        Map.of(
            "ids", ids,
            "with_vector", true,
            "with_payload", false);

    Map resp =
        qdrantWebClient
            .post()
            .uri("/collections/{name}/points", getPrefix(collectionName))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .onStatus(
                s -> s.is4xxClientError() || s.is5xxServerError(),
                r ->
                    r.bodyToMono(String.class)
                        .map(msg -> new CustomException(QdrantErrorCode.VECTOR_SIZE_MISMATCH)))
            .bodyToMono(Map.class)
            .block();

    List<Map<String, Object>> result = (List<Map<String, Object>>) resp.get("result");
    List<float[]> out = new ArrayList<>();
    for (Map<String, Object> p : result) {
      List<Double> v = (List<Double>) p.get("vector");
      float[] f = new float[v.size()];
      for (int i = 0; i < v.size(); i++) f[i] = v.get(i).floatValue();
      out.add(f);
    }
    return out;
  }

  @Override
  public List<Map<String, Object>> search(
      float[] query, int limit, List<Long> excludeIds, CollectionName collectionName) {
    if (query.length != qdrantConfig.getVectorSize())
      throw new CustomException(QdrantErrorCode.VECTOR_SIZE_MISMATCH);

    String objectId =
        switch (collectionName) {
          case PIECE -> "pieceId";
          case EXHIBITION -> "exhibitionId";
          case USER -> "userId";
        };

    List<Map<String, Object>> must = new ArrayList<>();
    must.add(Map.of("key", "lang", "match", Map.of("value", "ko")));

    List<Map<String, Object>> mustNot = new ArrayList<>();
    for (Long id : excludeIds) {
      mustNot.add(Map.of("key", objectId, "match", Map.of("value", id)));
    }

    Map<String, Object> filter = Map.of("must", must, "must_not", mustNot);

    Map<String, Object> params = new HashMap<>();
    params.put("exact", true);
    params.put("hnsw_ef", 512);

    Map<String, Object> body = new HashMap<>();
    body.put("vector", query);
    body.put("limit", limit);
    body.put("with_payload", true);
    body.put("params", params);
    body.put("score_threshold", 0.30);
    if (!must.isEmpty() || !mustNot.isEmpty()) body.put("filter", filter);

    Map resp =
        qdrantWebClient
            .post()
            .uri("/collections/{name}/points/search", getPrefix(collectionName))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    return (List<Map<String, Object>>) resp.getOrDefault("result", List.of());
  }

  /**
   * 컬렉션 이름을 통해서 qdrant에 해당 컬렉션을 생성(없으면 생성, 있으면 통과)
   *
   * @param collectionName 컬렉션 이름
   */
  private void createIfAbsent(CollectionName collectionName) {

    String collection = getPrefix(collectionName);

    Boolean exists =
        qdrantWebClient
            .get()
            .uri("/collections/{name}/exists", collection)
            .retrieve()
            .bodyToMono(Map.class)
            .map(m -> ((Map<?, ?>) m.get("result")))
            .map(r -> (Boolean) r.get("exists"))
            .onErrorReturn(false)
            .block();

    if (Boolean.TRUE.equals(exists)) return;

    Map<String, Object> body =
        Map.of(
            "vectors",
            Map.of(
                "size", qdrantConfig.getVectorSize(),
                "distance", qdrantConfig.getDistance()));

    qdrantWebClient
        .put()
        .uri("/collections/{name}", collection)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .onErrorResume(ex -> Mono.empty())
        .block();
  }

  /**
   * 컬렉션 이름을 통해 실제 지정된 컬렉션 값을 반환하는 메서드
   *
   * @param collectionName 컬렉션 이름
   * @return 실제 지정된 컬렉션 값
   */
  private String getPrefix(CollectionName collectionName) {
    return switch (collectionName) {
      case PIECE -> qdrantConfig.getPieceCollection();
      case EXHIBITION -> qdrantConfig.getExhibitionCollection();
      case USER -> qdrantConfig.getUserCollection();
    };
  }
}
