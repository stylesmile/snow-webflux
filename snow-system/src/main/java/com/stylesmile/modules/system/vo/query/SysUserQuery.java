package com.stylesmile.modules.system.vo.query;

import com.stylesmile.modules.system.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author chenye
 * @date 2018/12/10
 */
public class SysUserQuery extends PageImpl<SysUser> {

    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String departId;

    public SysUserQuery(List<SysUser> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }
}
