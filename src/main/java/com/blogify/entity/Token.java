package com.blogify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token")
@Builder
public class Token extends BaseEntity {

    @Column(name = "jwt_token", unique = true, nullable = false)
    private String token;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expire_date", nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
