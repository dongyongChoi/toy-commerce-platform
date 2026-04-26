package com.toyproject.member.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.common.web.GlobalExceptionHandler;
import com.toyproject.member.application.MemberService;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {
    @Mock
    private MemberService memberService;

    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new ObjectMapper()))
            .setValidator(validator)
            .build();
    }

    @AfterEach
    void tearDown() {
        validator.destroy();
    }

    @Test
    @DisplayName("회원 생성 요청이 유효하면 201 응답과 생성된 회원 정보를 반환한다")
    void createMember_returnsCreatedResponse() throws Exception {
        given(memberService.createMember(any(CreateMemberRequest.class)))
            .willReturn(new MemberResponse(1L, "Alice", "alice@example.com"));

        mockMvc.perform(
                post("/api/v1/members")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Alice",
                          "email": "alice@example.com"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("member created"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Alice"))
            .andExpect(jsonPath("$.data.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("회원 생성 요청의 이메일 형식이 잘못되면 400 응답을 반환한다")
    void createMember_withInvalidEmail_returnsBadRequest() throws Exception {
        mockMvc.perform(
                post("/api/v1/members")
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Alice",
                          "email": "invalid-email"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("email: email must be a valid email address"))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(memberService).shouldHaveNoInteractions();
    }
}
