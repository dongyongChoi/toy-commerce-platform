package com.toyproject.member.application;

import com.toyproject.member.domain.Member;
import com.toyproject.member.domain.MemberRepository;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Captor
    private ArgumentCaptor<Member> memberCaptor;

    @Test
    @DisplayName("회원 목록을 조회하면 회원 응답 목록으로 변환해 반환한다")
    void getMembers_returnsMappedResponses() {
        Member firstMember = new Member("Alice", "alice@example.com");
        Member secondMember = new Member("Bob", "bob@example.com");
        ReflectionTestUtils.setField(firstMember, "id", 1L);
        ReflectionTestUtils.setField(secondMember, "id", 2L);

        given(memberRepository.findAll()).willReturn(List.of(firstMember, secondMember));

        List<MemberResponse> result = memberService.getMembers();

        assertThat(result).containsExactly(
            new MemberResponse(1L, "Alice", "alice@example.com"),
            new MemberResponse(2L, "Bob", "bob@example.com")
        );
    }

    @Test
    @DisplayName("회원을 생성하면 저장된 회원 정보를 반환한다")
    void createMember_savesMemberAndReturnsResponse() {
        CreateMemberRequest request = new CreateMemberRequest("Alice", "alice@example.com");
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            ReflectionTestUtils.setField(member, "id", 1L);
            return member;
        });

        MemberResponse result = memberService.createMember(request);

        then(memberRepository).should().save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();
        assertThat(savedMember.getName()).isEqualTo("Alice");
        assertThat(savedMember.getEmail()).isEqualTo("alice@example.com");
        assertThat(result).isEqualTo(new MemberResponse(1L, "Alice", "alice@example.com"));
    }
}
