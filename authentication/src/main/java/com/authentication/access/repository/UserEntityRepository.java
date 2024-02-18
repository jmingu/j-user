package com.authentication.access.repository;


import com.common.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {

//    Optional<UserEntity> findByUserName(String userName);

     /**
      * 아이디 검증
      */
     long countByLoginId(String userName);

     /**
      * 회원정보 조회
      */
     Optional<UserEntity> findByLoginId(String loginId);
//    Optional<OrgEntity> findByOrgId(long orgId);
}
