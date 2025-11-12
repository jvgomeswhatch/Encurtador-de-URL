package com.urlshortener.service;

import com.urlshortener.dto.UrlRequestDto;
import com.urlshortener.dto.UrlResponseDto;
import com.urlshortener.entity.UrlEntity;
import com.urlshortener.exception.InvalidUrlException;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.exception.CustomCodeAlreadyExistsException;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlRepository urlRepository;
    private final UrlValidator urlValidator;

    @Value("${url.shortener.base-url}")
    private String baseUrl;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public UrlResponseDto shortenUrl(UrlRequestDto request) {
        String normalizedUrl = urlValidator.normalizeUrl(request.getUrl());

        // Valida formato da URL
        if (!urlValidator.isValidUrlFormat(normalizedUrl)) {
            throw new InvalidUrlException("Formato de URL inválido");
        }

        // Valida se a URL existe
        if (!urlValidator.urlExists(normalizedUrl)) {
            throw new InvalidUrlException("URL não existe ou não está acessível");
        }

        // Verifica se já existe encurtamento para esta URL
        Optional<UrlEntity> existing = urlRepository.findByOriginalUrl(normalizedUrl);
        if (existing.isPresent() && request.getCustomCode() == null) {
            return convertToDto(existing.get());
        }

        String shortCode;
        boolean isCustom = false;

        // Usa código personalizado se fornecido
        if (request.getCustomCode() != null && !request.getCustomCode().trim().isEmpty()) {
            shortCode = request.getCustomCode().trim();
            isCustom = true;

            // Verifica se o código personalizado já existe
            if (urlRepository.existsByShortCode(shortCode)) {
                throw new CustomCodeAlreadyExistsException(
                        "Código personalizado '" + shortCode + "' já está em uso"
                );
            }
        } else {
            // Gera código aleatório
            shortCode = generateShortCode();
        }

        UrlEntity urlEntity = new UrlEntity();
        urlEntity.setOriginalUrl(normalizedUrl);
        urlEntity.setShortCode(shortCode);
        urlEntity.setAccessCount(0L);
        urlEntity.setIsCustom(isCustom);
        urlEntity.setCreatedBy(request.getCreatedBy());

        urlEntity = urlRepository.save(urlEntity);

        return convertToDto(urlEntity);
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL não encontrada"));

        // Incrementa contador de acessos
        urlEntity.setAccessCount(urlEntity.getAccessCount() + 1);
        urlRepository.save(urlEntity);

        return urlEntity.getOriginalUrl();
    }

    public UrlResponseDto getUrlStats(String shortCode) {
        UrlEntity urlEntity = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL não encontrada"));

        return convertToDto(urlEntity);
    }

    private String generateShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    private UrlResponseDto convertToDto(UrlEntity entity) {
        UrlResponseDto dto = new UrlResponseDto();
        dto.setOriginalUrl(entity.getOriginalUrl());
        dto.setShortUrl(baseUrl + "/" + entity.getShortCode());
        dto.setShortCode(entity.getShortCode());
        dto.setAccessCount(entity.getAccessCount());
        dto.setCreatedAt(entity.getCreatedAt().toString());
        dto.setIsCustom(entity.getIsCustom());
        return dto;
    }
}