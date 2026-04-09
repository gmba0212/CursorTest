package com.example.eaimessage.service;

import org.springframework.stereotype.Service;

@Service
public class DefaultShortUrlService implements ShortUrlService {
    @Override
    public String createShortUrl(String longUrl) {
        String source = longUrl == null || longUrl.isBlank()
            ? "https://example.com/default"
            : longUrl;
        return "https://sho.rt/" + Integer.toHexString(source.hashCode());
    }
}
