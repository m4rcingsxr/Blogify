package com.blogify.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class EntityDto implements Serializable {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

}
