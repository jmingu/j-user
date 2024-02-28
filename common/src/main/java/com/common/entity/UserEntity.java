package com.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name="tb_user")
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String loginId;

    private String userName;

    private String password;

    private String email;

    private String gender;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organizationEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "login_type_id")
    private LoginTypeEntity loginTypeEntity;

    @OneToMany(mappedBy = "userEntity")
    private List<LoginHistoryEntity> loginHistoryEntity;

    public UserEntity(String loginId, String userName, String password, String email, String gender, OrganizationEntity organizationEntity, LoginTypeEntity loginTypeEntity) {
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.organizationEntity = organizationEntity;
        this.loginTypeEntity = loginTypeEntity;
        setCreateBy("SYSTEM");
    }


//
//    @PreUpdate
//    void updatedAt() {
//        this.updatedAt = Timestamp.from(Instant.now());
//    }



}
