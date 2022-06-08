package com.snow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginVo {
    @Schema(description = "用户名",example = "admin")
    private String username;
    @Schema(description = "密码",example = "123456")
    private String password;
}
