package com.user.information.repository;

import com.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserInfomationRepository extends JpaRepository<UserEntity, Long> {
    /**
     * 회원정보 조회
     */
    Optional<UserEntity> findByLoginId(String loginId);
}
