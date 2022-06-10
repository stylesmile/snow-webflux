package com.snow.generator.service;

import java.util.List;

import com.snow.common.biz.IService;
import com.snow.generator.domain.GenTableColumn;
import reactor.core.publisher.Mono;

/**
 * 业务字段 服务层
 * 
 * @author ruoyi
 */
public interface IGenTableColumnService extends IService<GenTableColumn, Long>
{
    /**
     * 查询业务字段列表
     * 
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    public Mono<List<GenTableColumn>> selectGenTableColumnListByTableId(Long tableId);

}
