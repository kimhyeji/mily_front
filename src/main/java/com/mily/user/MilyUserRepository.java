package com.mily.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MilyUserRepository extends JpaRepository<MilyUser, Long> {
    Optional<MilyUser> findByUserLoginId (String userLoginId);
    Optional<MilyUser> findByUserEmail (String userEmail);
    Optional<MilyUser> findByUserPhoneNumber (String userPhoneNumber);
    Optional<MilyUser> findByUserName(String userName);

    Optional<MilyUser> findByUserLoginIdAndUserEmail(String userLoginId, String userEmail);

    @Query(value = "SELECT * FROM mily_user WHERE user_email = :userEmail", nativeQuery = true)
    MilyUser findUserLoginIdByEmail(@Param("userEmail") String userEmail); // 이 메소드만 사용
    Optional<MilyUser> findByUserLoginIdAndRole(String userLoginId, String role);
    List<MilyUser> findByRole(String role);

    @Modifying
    @Query("UPDATE MilyUser u SET u.userPassword = :newUserPassword WHERE u.id = :userId")
    int updateUserPassword(@Param("userId") Long userId, @Param("newUserPassword") String newUserPassword);
}