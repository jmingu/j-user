package com.common.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String loginType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private OrganizationEntity organizationEntity;

    public UserEntity(String loginId, String userName, String password, String email, String gender, String loginType, OrganizationEntity organizationEntity) {
        this.loginId = loginId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.gender = gender;
        this.loginType = loginType;
        this.organizationEntity = organizationEntity;
        setCreateBy("SYSTEM");
    }


//
//    @PreUpdate
//    void updatedAt() {
//        this.updatedAt = Timestamp.from(Instant.now());
//    }



}
