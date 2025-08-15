/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.likelion13.artium.domain.user.mapping.UserLike;

@Repository
public interface UserLikeRepository extends JpaRepository<UserLike, Long> {}
