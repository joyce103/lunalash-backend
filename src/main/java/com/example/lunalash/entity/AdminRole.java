package com.example.lunalash.entity;

public enum AdminRole {
    ADMIN,       // 一般管理員：新增/刪除其他管理員帳號需送出申請，等最高管理員審核
    SUPER_ADMIN  // 最高管理員：可直接新增/刪除帳號，並審核一般管理員送出的申請
}
