package com.user.information.repository;

import com.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfomationRepository extends JpaRepository<UserEntity, Long> {
    /**
     * 회원정보 조회(login_id)
     */
    UserEntity findByLoginId(String loginId);

    /**
     * 회원정보 조회(user_id)
     */
    UserEntity findByUserId(Long userId);

    /**
     * 회원정보 조회
     */
    @Query("select user from UserEntity as user where user.userId in :userIdList")
    List<UserEntity> findByLoginList(@Param("userIdList") List<Long> userIdList);
}
