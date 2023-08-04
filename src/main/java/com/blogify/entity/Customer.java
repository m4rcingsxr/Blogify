package com.blogify.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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
