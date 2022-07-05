package com.snow.web.service;

import com.snow.common.biz.IService;
import com.snow.web.domain.SysOperLog;

/**
 * 操作日志 服务层
 * 
 * @author ruoyi
 */
public interface ISysOperLogService extends IService<SysOperLog, Long>
{
    /**
     * 清空操作日志
     */
    public void cleanOperLog();
}
