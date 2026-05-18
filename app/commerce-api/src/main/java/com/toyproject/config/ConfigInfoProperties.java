package com.toyproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = "toy-commerce.config")
public class ConfigInfoProperties {
    private String source = "local";
    private String message = "로컬 기본 설정입니다.";
}
