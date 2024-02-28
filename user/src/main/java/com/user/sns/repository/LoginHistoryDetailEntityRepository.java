package com.user.sns.repository;

import com.common.entity.LoginHistoryDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistoryDetailEntityRepository extends JpaRepository<LoginHistoryDetailEntity, Long> {

}
