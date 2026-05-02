package com.toyproject.config;

public record ConfigInfoResponse(
    String source,
    String message
) {
    public static ConfigInfoResponse from(ConfigInfoProperties properties) {
        return new ConfigInfoResponse(properties.getSource(), properties.getMessage());
    }
}
