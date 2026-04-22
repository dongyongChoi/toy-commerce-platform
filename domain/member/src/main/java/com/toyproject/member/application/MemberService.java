package com.toyproject.member.application;

import com.toyproject.member.domain.Member;
import com.toyproject.member.domain.MemberRepository;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        Member member = memberRepository.save(new Member(request.name(), request.email()));
        return MemberResponse.from(member);
    }
}

