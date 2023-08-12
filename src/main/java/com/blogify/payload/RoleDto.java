package com.blogify.payload;

import com.blogify.entity.Role;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

@Data
public class RoleDto implements Serializable {
    Long id;
    String name;
    String description;
}