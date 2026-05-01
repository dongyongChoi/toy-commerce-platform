package com.toyproject.member.web;

import com.toyproject.common.web.ApiResponse;
import com.toyproject.member.application.MemberService;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import com.toyproject.member.web.dto.UpdateMemberRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ApiResponse<List<MemberResponse>> getMembers() {
        return ApiResponse.success(memberService.getMembers());
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse> getMember(@PathVariable("memberId") Long memberId) {
        return ApiResponse.success(memberService.getMember(memberId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        return ApiResponse.success(memberService.createMember(request), "member created");
    }

    @PutMapping("/{memberId}")
    public ApiResponse<MemberResponse> updateMember(
        @PathVariable("memberId") Long memberId,
        @Valid @RequestBody UpdateMemberRequest request
    ) {
        return ApiResponse.success(memberService.updateMember(memberId, request), "member updated");
    }

    @DeleteMapping("/{memberId}")
    public ApiResponse<Void> deleteMember(@PathVariable("memberId") Long memberId) {
        memberService.deleteMember(memberId);
        return ApiResponse.success(null, "member deleted");
    }
}

