package com.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name="tb_login_history")
public class LoginHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;
    private LocalDate loginYmd;

    public LoginHistoryEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.loginYmd = LocalDate.now();
        setCreateDate(LocalDateTime.now());
        setCreateBy("user_id : " + userEntity.getUserId());
    }

}
