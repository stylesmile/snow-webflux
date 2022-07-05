package com.snow.web.service.impl;

import com.snow.common.biz.BaseServiceImpl;
import com.snow.web.mapper.SysNoticeMapper;
import com.snow.web.service.ISysNoticeService;
import org.springframework.stereotype.Service;
import com.snow.web.domain.SysNotice;

/**
 * 公告 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl
	extends BaseServiceImpl<SysNotice, Long, SysNoticeMapper>
	implements ISysNoticeService
{

}
