package com.toyproject.member.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.member.domain.Member;
import com.toyproject.member.domain.MemberRepository;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import com.toyproject.member.web.dto.UpdateMemberRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("회원을 생성하면 저장된 회원 응답을 반환한다")
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

    @Test
    @DisplayName("회원 단건을 조회하면 회원 응답을 반환한다")
    void getMember_returnsMemberResponse() {
        Member member = new Member("Alice", "alice@example.com");
        ReflectionTestUtils.setField(member, "id", 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        MemberResponse result = memberService.getMember(1L);

        assertThat(result).isEqualTo(new MemberResponse(1L, "Alice", "alice@example.com"));
    }

    @Test
    @DisplayName("존재하지 않는 회원을 조회하면 예외가 발생한다")
    void getMember_whenMissing_throwsDomainException() {
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMember(999L))
            .isInstanceOf(DomainException.class)
            .hasMessage("member not found");
    }

    @Test
    @DisplayName("회원을 수정하면 변경된 회원 응답을 반환한다")
    void updateMember_updatesMemberAndReturnsResponse() {
        Member member = new Member("Alice", "alice@example.com");
        ReflectionTestUtils.setField(member, "id", 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        MemberResponse result = memberService.updateMember(
            1L,
            new UpdateMemberRequest("Alice Updated", "alice.updated@example.com")
        );

        assertThat(member.getName()).isEqualTo("Alice Updated");
        assertThat(member.getEmail()).isEqualTo("alice.updated@example.com");
        assertThat(result).isEqualTo(new MemberResponse(1L, "Alice Updated", "alice.updated@example.com"));
    }

    @Test
    @DisplayName("회원을 삭제하면 저장소에서 제거한다")
    void deleteMember_deletesMember() {
        Member member = new Member("Alice", "alice@example.com");
        ReflectionTestUtils.setField(member, "id", 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));

        memberService.deleteMember(1L);

        then(memberRepository).should().delete(member);
    }
}
