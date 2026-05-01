package com.toyproject.member.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.member.domain.Member;
import com.toyproject.member.domain.MemberRepository;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import com.toyproject.member.web.dto.UpdateMemberRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

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
        Member member = memberRepository.save(new Member(request.name(), request.email()));
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = findMember(memberId);
        member.update(request.name(), request.email());
        return MemberResponse.from(member);
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
}

