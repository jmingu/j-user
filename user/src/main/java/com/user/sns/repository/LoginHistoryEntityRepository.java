package com.user.sns.repository;

import com.common.entity.LoginHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LoginHistoryEntityRepository extends JpaRepository<LoginHistoryEntity, Long> {

    /**
     * 로그인 이력 조회
     */
    @Query("select history from LoginHistoryEntity as history where history.userEntity.userId = :userId and history.loginYmd = :loginYmd")
    LoginHistoryEntity findLoginHistory(@Param("userId")Long userId, @Param("loginYmd")LocalDate loginYmd);

}
