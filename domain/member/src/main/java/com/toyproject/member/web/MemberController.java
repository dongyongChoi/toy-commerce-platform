package com.toyproject.member.web;

import com.toyproject.common.web.ApiResponse;
import com.toyproject.member.application.MemberService;
import com.toyproject.member.web.dto.CreateMemberRequest;
import com.toyproject.member.web.dto.MemberResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        return ApiResponse.success(memberService.createMember(request), "member created");
    }
}

