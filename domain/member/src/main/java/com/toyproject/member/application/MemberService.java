package com.toyproject.member.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.member.domain.Member;
import com.toyproject.member.domain.MemberRepository;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import com.toyproject.member.web.dto.UpdateMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public List<MemberResponse> getMembers() {
        return memberRepository.findAll()
            .stream()
            .map(MemberResponse::from)
            .toList();
    }

    public MemberResponse getMember(Long memberId) {
        return MemberResponse.from(findMember(memberId));
    }

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        validateNewEmail(request.email());

        try {
            Member member = memberRepository.saveAndFlush(new Member(request.name(), request.email()));
            return MemberResponse.from(member);
        } catch (DataIntegrityViolationException exception) {
            throw duplicateEmailException();
        }
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = findMember(memberId);
        validateUpdateEmail(request.email(), memberId);

        try {
            member.update(request.name(), request.email());
            memberRepository.flush();
            return MemberResponse.from(member);
        } catch (DataIntegrityViolationException exception) {
            throw duplicateEmailException();
        }
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = findMember(memberId);
        memberRepository.delete(member);
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "member not found"));
    }

    private void validateNewEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw duplicateEmailException();
        }
    }

    private void validateUpdateEmail(String email, Long memberId) {
        if (memberRepository.existsByEmailAndIdNot(email, memberId)) {
            throw duplicateEmailException();
        }
    }

    private DomainException duplicateEmailException() {
        return new DomainException(ErrorCode.DUPLICATE_RESOURCE, "이미 사용 중인 이메일입니다.");
    }
}
