package com.toyproject.config;

import com.toyproject.common.web.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigInfoController.class)
@Import(GlobalExceptionHandler.class)
class ConfigInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfigInfoProperties configInfoProperties;

    @Test
    @DisplayName("설정 정보 조회 요청이 유효하면 현재 설정 출처와 메시지를 반환한다")
    void getConfigInfo_returnsCurrentConfigurationInfo() throws Exception {
        given(configInfoProperties.getSource()).willReturn("config-server");
        given(configInfoProperties.getMessage()).willReturn("Spring Cloud Config에서 전달된 설정입니다.");

        mockMvc.perform(get("/api/v1/config"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.source").value("config-server"))
            .andExpect(jsonPath("$.data.message").value("Spring Cloud Config에서 전달된 설정입니다."));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(ConfigInfoController.class)
    static class TestApplication {
    }
}
