package com.stylesmile.system.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stylesmile.system.entity.SysUser;
import lombok.Data;

/**
 * @author chenye
 * @date 2018/12/10
 */
@Data
public class SysUserQuery extends Page<SysUser> {

    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String email;

}