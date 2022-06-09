package com.stylesmile.modules.log.entity;


import java.util.Date;

/**
 * @author chenye
 * @date 2019/2/26
 */
public class LogLogin {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 登陆用户名
     */
    private Integer userId;
    /**
     * 昵称
     */
    private String username;
    /**
     * ip
     */
    private String ip;
    /**
     * 状态，
     * 0失败
     * 1成功
     */
    private int status;
    ;
    /**
     * 秘密啊
     */
    private Date createTime;

    public LogLogin(String name, Integer userId, String username, String ip, int status) {
        this.name = name;
        this.userId = userId;
        this.username = username;
        this.ip = ip;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public LogLogin() {
    }

}
