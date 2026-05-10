package com.toyproject.member.web;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.common.web.GlobalExceptionHandler;
import com.toyproject.member.application.MemberService;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import com.toyproject.member.web.dto.UpdateMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import(GlobalExceptionHandler.class)
class MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

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

    @Test
    @DisplayName("이미 사용 중인 이메일로 회원 생성 시 409 응답을 반환한다")
    void createMember_whenEmailDuplicated_returnsConflict() throws Exception {
        given(memberService.createMember(any(CreateMemberRequest.class)))
            .willThrow(new DomainException(ErrorCode.DUPLICATE_RESOURCE, "이미 사용 중인 이메일입니다."));

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
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."))
            .andExpect(jsonPath("$.errorCode").value("COMMON-409"));
    }

    @Test
    @DisplayName("회원 단건 조회 요청이 유효하면 회원 정보를 반환한다")
    void getMember_returnsMemberResponse() throws Exception {
        given(memberService.getMember(1L))
            .willReturn(new MemberResponse(1L, "Alice", "alice@example.com"));

        mockMvc.perform(get("/api/v1/members/{memberId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Alice"))
            .andExpect(jsonPath("$.data.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("회원 수정 요청이 유효하면 수정된 회원 정보를 반환한다")
    void updateMember_returnsUpdatedResponse() throws Exception {
        given(memberService.updateMember(any(Long.class), any(UpdateMemberRequest.class)))
            .willReturn(new MemberResponse(1L, "Alice Updated", "alice.updated@example.com"));

        mockMvc.perform(
                put("/api/v1/members/{memberId}", 1L)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Alice Updated",
                          "email": "alice.updated@example.com"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("member updated"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Alice Updated"))
            .andExpect(jsonPath("$.data.email").value("alice.updated@example.com"));
    }

    @Test
    @DisplayName("이미 사용 중인 이메일로 회원 수정 시 409 응답을 반환한다")
    void updateMember_whenEmailDuplicated_returnsConflict() throws Exception {
        given(memberService.updateMember(any(Long.class), any(UpdateMemberRequest.class)))
            .willThrow(new DomainException(ErrorCode.DUPLICATE_RESOURCE, "이미 사용 중인 이메일입니다."));

        mockMvc.perform(
                put("/api/v1/members/{memberId}", 1L)
                    .contentType(APPLICATION_JSON)
                    .content("""
                        {
                          "name": "Alice Updated",
                          "email": "bob@example.com"
                        }
                        """)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."))
            .andExpect(jsonPath("$.errorCode").value("COMMON-409"));
    }

    @Test
    @DisplayName("회원 삭제 요청이 유효하면 삭제 완료 응답을 반환한다")
    void deleteMember_returnsDeletedResponse() throws Exception {
        mockMvc.perform(delete("/api/v1/members/{memberId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("member deleted"));

        then(memberService).should().deleteMember(1L);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(MemberController.class)
    static class TestApplication {
    }
}
