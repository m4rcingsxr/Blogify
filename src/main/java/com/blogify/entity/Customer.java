package com.blogify.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {


    @Column(name = "email", unique = true, nullable = false, length = 255)
    public String email;

    @Column(name = "password", nullable = false, length = 255)
    public String password;

    @Column(name = "first_name", nullable = false, length = 255)
    public String firstName;

    @Column(name = "last_name", nullable = false, length = 255)
    public String lastName;


}
