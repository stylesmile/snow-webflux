package com.stylesmile.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stylesmile.system.entity.SysUser;
import com.stylesmile.system.query.SysUserQuery;
import com.stylesmile.system.service.SysRoleService;
import com.stylesmile.system.service.SysUserService;
import com.stylesmile.util.Result;
import com.stylesmile.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户管理
 *
 * @author StyleSmile
 * @date 2018/12/08
 */
@RestController
public class SysUserController {

    private final String BASE_URL_PATH = "/user";
    private final String BASE_HTML_PATH = "/system/sysUser";
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 菜单管理
     */
    @GetMapping(BASE_URL_PATH + "/index.html")
    @ResponseBody
    public ModelAndView index() {
        ModelAndView view = new ModelAndView(BASE_HTML_PATH + "/user");
        return view;
    }

    /**
     * 首页数据
     */
    @GetMapping(BASE_URL_PATH + "/list.json")
    public Result selectUserPage(SysUserQuery sysUserQuery) {
        Page<SysUser> page = sysUserService.getUserList(sysUserQuery);
        return Result.success(page);
    }

    /**
     * 进入新增用户页面
     */
    @GetMapping(BASE_URL_PATH + "/add.html")
    @ResponseBody
    public ModelAndView add() {
        ModelAndView view = new ModelAndView(BASE_HTML_PATH + "/user_add");
        return view;
    }

    /**
     * 新增用户
     */
    @PostMapping(BASE_URL_PATH + "/add.json")
    @ResponseBody
    public Result add(SysUser user) {
        user.setId(UUIDUtil.getUUID());
        Boolean b = sysUserService.saveOrUpdate(user);
        if (b) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    /**
     * 进入编辑用户页面
     *
     * @param id 主键
     */
    @GetMapping(BASE_URL_PATH + "/edit.html")
    public ModelAndView edit(String id) {
        ModelAndView view = new ModelAndView(BASE_HTML_PATH + "/user_edit");
        SysUser user = sysUserService.getById(id);
        view.addObject("user", user);
        return view;
    }

    /**
     * 编辑用户
     */
    @PostMapping(BASE_URL_PATH + "/edit.json")
    @ResponseBody
    public Result edit(SysUser user) {
        Boolean b = sysUserService.updateUser(user);
        if (b) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    /**
     * 删除用户
     */
    @PostMapping(BASE_URL_PATH + "/delete.json")
    @ResponseBody
    public Result delete(String id) {
        Boolean b = sysUserService.deleteUser(id);
        if (b) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

}