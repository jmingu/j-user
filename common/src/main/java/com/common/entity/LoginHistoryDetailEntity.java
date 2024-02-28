package com.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name="tb_login_history_detail")
public class LoginHistoryDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginHistoryDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_history_id")
    private LoginHistoryEntity loginHistoryEntity;

    private LocalDateTime loginDate;

    public LoginHistoryDetailEntity(LoginHistoryEntity loginHistoryEntity, Long userId) {
        this.loginHistoryEntity = loginHistoryEntity;
        this.loginDate = LocalDateTime.now();
        setCreateDate(LocalDateTime.now());
        setCreateBy("user_id : " + userId);
    }

}
