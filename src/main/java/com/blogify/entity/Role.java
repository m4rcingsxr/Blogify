package com.blogify.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    private String name;

    private String description;



}
