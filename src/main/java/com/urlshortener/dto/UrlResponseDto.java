package com.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponseDto { // <--- ADICIONADO "public"
    private String originalUrl;
    private String shortUrl;
    private String shortCode;
    private Long accessCount;
    private String createdAt;
    private Boolean isCustom;
}