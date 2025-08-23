/* 
 * Copyright (c) LikeLion13th Problem not Found 
 */
package com.likelion13.artium.domain.user.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.likelion13.artium.domain.exhibition.entity.ExhibitionStatus;
import com.likelion13.artium.domain.piece.entity.ProgressStatus;
import com.likelion13.artium.domain.user.entity.Age;
import com.likelion13.artium.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  List<User> findByNicknameContaining(String keyword);

  List<User> findByCodeContaining(String code);

  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);

  @Query(
      "SELECT u FROM User u LEFT JOIN u.likedByUsers l "
          + "WHERE u.id != :userId "
          + "GROUP BY u "
          + "ORDER BY COUNT(l) DESC")
  Page<User> findHottestCreators(@Param("userId") Long userId, Pageable pageable);

  @Query(
      """
  SELECT u
  FROM User u
  JOIN u.exhibitionParticipants ep
  JOIN ep.exhibition e
  WHERE e.startDate > :startDate
    AND e.exhibitionStatus = :status
    AND e.fillAll = true
    AND u.id != :userId
  GROUP BY u
  ORDER BY MAX(e.startDate) DESC
""")
  Page<User> findRecentOngoingExhibitionUsers(
      @Param("userId") Long userId,
      @Param("startDate") LocalDate startDate,
      @Param("status") ExhibitionStatus status,
      Pageable pageable);

  @Query("SELECT u FROM User u LEFT JOIN u.pieces p " +
      "WHERE u.age = :age AND u.id <> :id AND p.progressStatus NOT IN :statuses " +
      "GROUP BY u.id " +
      "ORDER BY MAX(p.createdAt) DESC")
  Page<User> findSameAgeUsers(
      @Param("userId") Long userId,
      @Param("age") Age age,
      @Param("statuses") List<ProgressStatus> statuses,
      Pageable pageable);

  Page<User> findByCodeContaining(String code, Pageable pageable);
}
