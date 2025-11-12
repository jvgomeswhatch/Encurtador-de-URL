package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {

    @NotBlank(message = "URL não pode estar vazia")
    private String url;

    @Size(min = 3, max = 20, message = "Código personalizado deve ter entre 3 e 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "Código personalizado deve conter apenas letras, números, hífens e underscores")
    private String customCode;

    private String createdBy;
}