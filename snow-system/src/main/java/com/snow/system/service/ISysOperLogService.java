package com.snow.system.service;

import com.snow.common.biz.IService;
import com.snow.system.domain.SysOperLog;

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
