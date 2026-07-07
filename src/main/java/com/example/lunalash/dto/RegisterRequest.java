package com.example.lunalash.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "帳號不能為空")
    @Size(max = 50, message = "帳號長度不能超過 50 字")
    private String username;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 100, message = "密碼長度至少需要 6 個字")
    private String password;

    @NotBlank(message = "姓名不能為空")
    @Size(max = 50, message = "姓名長度不能超過 50 字")
    private String name;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
