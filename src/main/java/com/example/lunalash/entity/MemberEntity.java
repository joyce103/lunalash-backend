package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class MemberEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // 會員編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    // 姓名
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    // 性別 M / F / O
    @Column(name = "gender", nullable = false, length = 1)
    private String gender;

    // 生日
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    // 手機號碼
    @Column(name = "phone", nullable = false, unique = true, length = 10)
    private String phone;

    // 會員等級
    @Column(name = "member_level", nullable = false, length = 10)
    private String memberLevel;

    // LINE ID
    @Column(name = "line_id", length = 50)
    private String lineId;

    // 建立時間（由 DB 自動產生）
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    // 更新時間（由 DB 自動更新）
    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // ===== Getter / Setter =====

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
