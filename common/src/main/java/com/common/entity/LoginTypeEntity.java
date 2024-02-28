package com.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tb_login_type")
public class LoginTypeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long loginTypeId;

    private String loginType;

    private String loginTypeName;


    @OneToMany(mappedBy = "loginTypeEntity")
    private List<UserEntity> userEntity;
}
