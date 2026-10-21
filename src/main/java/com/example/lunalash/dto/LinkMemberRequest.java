package com.example.lunalash.dto;

public class LinkMemberRequest {

    // null 代表取消連結，改成訪客身分
    private Long memberId;

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }
}
