package com.blogify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
public class Token extends BaseEntity {

    @Column(name = "jwt_token", unique = true, nullable = false)
    private String token;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "validation_date", nullable = false)
    private LocalDateTime validateAt;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
