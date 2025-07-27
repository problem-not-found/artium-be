/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ArtiumApplication {

  public static void main(String[] args) {
    SpringApplication.run(ArtiumApplication.class, args);
  }
}
