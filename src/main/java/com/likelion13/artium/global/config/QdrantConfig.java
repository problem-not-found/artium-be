/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import reactor.netty.http.client.HttpClient;

@Getter
@Configuration
public class QdrantConfig {

  @Value("${qdrant.collection-name.piece}")
  private String pieceCollection;

  @Value("${qdrant.collection-name.exhibition}")
  private String exhibitionCollection;

  @Value("${qdrant.collection-name.user}")
  private String userCollection;

  @Value("${qdrant.vector-size}")
  private int vectorSize;

  @Value("${qdrant.distance:Cosine}")
  private String distance;

  @Bean
  public WebClient qdrantWebClient(
      @Value("${qdrant.base-url}") String baseUrl, @Value("${qdrant.api-key:}") String apiKey) {
    HttpClient http = HttpClient.create().responseTimeout(Duration.ofSeconds(10)).compress(true);

    WebClient.Builder b =
        WebClient.builder()
            .baseUrl(baseUrl)
            .clientConnector(new ReactorClientHttpConnector(http))
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                    .build());

    if (!apiKey.isBlank()) {
      b.defaultHeader("api-key", apiKey);
    }
    b.defaultHeader("Accept", "application/json");

    return b.build();
  }
}
