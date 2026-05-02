package com.toyproject.config;

import com.toyproject.common.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/config")
public class ConfigInfoController {
    private final ConfigInfoProperties configInfoProperties;

    @GetMapping
    public ApiResponse<ConfigInfoResponse> getConfigInfo() {
        return ApiResponse.success(ConfigInfoResponse.from(configInfoProperties));
    }
}
