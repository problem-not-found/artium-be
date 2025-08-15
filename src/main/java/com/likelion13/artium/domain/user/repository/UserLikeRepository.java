/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion13.artium.domain.user.entity.User;
import com.likelion13.artium.domain.user.mapping.UserLike;

public interface UserLikeRepository extends JpaRepository<UserLike, Long> {

  @Query("select ul.liked " + "from UserLike ul " + "where ul.liker.id = :likerId")
  Page<User> findLikedUserByLikerId(@Param("likerId") Long likerId, Pageable pageable);
}
