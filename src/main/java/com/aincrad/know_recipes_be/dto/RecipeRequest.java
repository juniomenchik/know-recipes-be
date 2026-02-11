package com.aincrad.know_recipes_be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título não pode ter mais de 200 caracteres")
    private String title;

    @Size(max = 5000, message = "Descrição muito longa")
    private String description;

    private String imageUrl;

    private Boolean isPrivate = false;
}
