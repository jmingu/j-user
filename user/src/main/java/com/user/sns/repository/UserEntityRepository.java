package com.user.sns.repository;

import com.common.entity.LoginTypeEntity;
import com.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

     /**
      * 로그인 타입 검색
      */
     @Query("select loginType from LoginTypeEntity as loginType where loginType.loginType = :loginType")
     LoginTypeEntity findLoginType(@Param("loginType") String loginType);

     /**
      * 아이디 검증
      */
     long countByLoginId(String userName);

     /**
      * 회원정보 조회
      */
     UserEntity findByLoginId(String loginId);

     /**
      * 로그인 이력 등록
      */
     @Modifying
     @Query(value = "INSERT INTO tb_login_history (user_id, login_ymd, create_date, create_by) VALUES (:userId, :localDate, :localDateTime, :createBy) ON CONFLICT (user_id, login_ymd) DO UPDATE SET update_date = :localDateTime, update_dy = :createBy", nativeQuery = true)
     int saveLoginHistory(@Param("userId")Long userId, @Param("localDate")LocalDate localDate, @Param("localDateTime")LocalDateTime localDateTime, @Param("createBy")String createBy);

     /**
      * 로그인 이력  상세 등록
      */
     @Modifying
     @Query(value = "INSERT INTO tb_login_history_detail (login_history_id, login_date, create_date, create_by) VALUES (:userId, :localDate, :localDateTime, :createBy) ON CONFLICT (user_id, login_ymd) DO UPDATE SET update_date = :localDateTime, update_dy = :createBy", nativeQuery = true)
     int saveLoginHistoryDetail(@Param("userId")Long userId, @Param("localDate")LocalDate localDate, @Param("localDateTime")LocalDateTime localDateTime, @Param("createBy")String createBy);

}
