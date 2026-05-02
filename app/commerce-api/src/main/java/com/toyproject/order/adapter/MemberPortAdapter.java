package com.toyproject.order.adapter;

import com.toyproject.member.domain.MemberRepository;
import com.toyproject.order.application.port.MemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberPortAdapter implements MemberPort {
    private final MemberRepository memberRepository;

    @Override
    public boolean exists(Long memberId) {
        return memberRepository.existsById(memberId);
    }
}
